package com.nielsmasdorp.domain.order

import com.nhaarman.mockitokotlin2.*
import com.nielsmasdorp.domain.notifications.MessageProvider
import com.nielsmasdorp.domain.notifications.SendNotification
import com.nielsmasdorp.domain.settings.SettingsRepository
import com.nielsmasdorp.domain.stock.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CheckNewLVBStateTest {

    @Mock
    lateinit var getStock: GetStock

    @Mock
    lateinit var getLastKnownStock: GetLastKnownStock

    @Mock
    lateinit var setLastKnownStock: SetLastKnownStock

    @Mock
    lateinit var setBuyBoxStatus: SetBuyBoxStatus

    @Mock
    lateinit var getBuyBoxStatus: GetBuyBoxStatus

    @Mock
    lateinit var messageProvider: MessageProvider

    @Mock
    lateinit var sendNotification: SendNotification

    @Mock
    lateinit var settingsRepository: SettingsRepository

    @Test
    fun `when no stock, do nothing`() = runBlockingTest {
        // given
        given(getStock.invoke()).willReturn(emptyList())

        // when
        val subject = createGetLVBOverview()
        subject.invoke()

        // then
        verify(sendNotification, never()).invoke(any(), any())
    }

    @Test
    fun `when single item in stock and no known value, send no messages and write stock value`() =
        runBlockingTest {
            // given
            val productId = "1"
            val productTitle = "Cheese"
            val stockAmount = 20
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, productTitle, stockAmount, true))
            )
            given(getLastKnownStock.invoke(productId)).willReturn(-1)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(false)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(false)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(setLastKnownStock).invoke(productId, stockAmount)
            verify(sendNotification, never()).invoke(any(), any())
        }


    @Test
    fun `when single item in stock with one lower value and order notifications are on, send single order message and write stock value`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            val stockAmount = 0
            val knownStockAmount = 1
            val message = "%s %s %s"
            val fullMessage = "1 $title $stockAmount"
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, stockAmount, true))
            )
            given(getLastKnownStock.invoke(productId)).willReturn(knownStockAmount)
            given(messageProvider.getOrderMessage()).willReturn(message)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(false)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(true)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(setLastKnownStock).invoke(productId, stockAmount)
            verify(sendNotification).invoke(title, fullMessage)
        }

    @Test
    fun `when single item in stock with one lower value and order notifications are off, send no order message and write stock value`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            val stockAmount = 0
            val knownStockAmount = 1
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, stockAmount, true))
            )
            given(getLastKnownStock.invoke(productId)).willReturn(knownStockAmount)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(false)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(false)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(setLastKnownStock).invoke(productId, stockAmount)
            verify(sendNotification, never()).invoke(any(), any())
        }

    @Test
    fun `when single item in stock with two lower value, send two order messages and write stock value`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            val stockAmount = 1
            val knownStockAmount = 3
            val message = "%s %s %s"
            val fullMessage = "2 $title $stockAmount"
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, stockAmount, true))
            )
            given(getLastKnownStock.invoke(productId)).willReturn(knownStockAmount)
            given(messageProvider.getOrderMessage()).willReturn(message)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(false)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(true)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(sendNotification).invoke(title, fullMessage)
            verify(setLastKnownStock).invoke(productId, stockAmount)
        }

    @Test
    fun `when single item in stock with three lower value, send three messages and write stock value`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            val stockAmount = 1
            val knownStockAmount = 4
            val message = "%s %s %s"
            val fullMessage = "3 $title $stockAmount"
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, stockAmount, true))
            )
            given(getLastKnownStock.invoke(productId)).willReturn(knownStockAmount)
            given(messageProvider.getOrderMessage()).willReturn(message)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(false)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(true)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(sendNotification).invoke(title, fullMessage)
            verify(setLastKnownStock).invoke(productId, stockAmount)
        }

    @Test
    fun `when item previously did not have buy box but now it does, write new value and send message`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            val message = "%s"
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, 1, true))
            )
            given(getBuyBoxStatus.invoke(productId)).willReturn(false)
            given(getLastKnownStock.invoke(productId)).willReturn(1)
            given(messageProvider.getBuyBoxGainedMessage()).willReturn(message)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(true)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(false)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(setBuyBoxStatus).invoke(productId, true)
            verify(sendNotification).invoke(title, title)
        }

    @Test
    fun `when item previously did have buy box but now it does not, write new value and send message`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            val message = "%s"
            given(getStock.invoke()).willReturn(
                listOf(
                    Stock(productId, title, 1, false)
                )
            )
            given(getBuyBoxStatus.invoke(productId)).willReturn(true)
            given(getLastKnownStock.invoke(productId)).willReturn(1)
            given(messageProvider.getBuyBoxLostMessage()).willReturn(message)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(true)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(false)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(setBuyBoxStatus).invoke(productId, false)
            verify(sendNotification).invoke(title, title)
        }

    @Test
    fun `when item previously did have buy box but now it does not and check is disabled, do not write new value and do not send message`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, 1, false))
            )
            given(getLastKnownStock.invoke(productId)).willReturn(1)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(false)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(false)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verifyNoMoreInteractions(setBuyBoxStatus)
            verifyNoMoreInteractions(sendNotification)
        }

    @Test
    fun `when item previously did not have buy box but now it does and check is disabled, do not write new value and do not send message`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, 1, true))
            )
            given(getLastKnownStock.invoke(productId)).willReturn(1)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(false)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(false)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verifyNoMoreInteractions(setBuyBoxStatus)
            verifyNoMoreInteractions(sendNotification)
        }

    @Test
    fun `when item previously did not have buy box status but now it does, do not send message, but do write value`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, 0, true))
            )
            given(getBuyBoxStatus.invoke(productId)).willReturn(null)
            given(getLastKnownStock.invoke(productId)).willReturn(0)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(true)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(false)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(setBuyBoxStatus).invoke(productId, true)
            verifyZeroInteractions(sendNotification)
        }

    @Test
    fun `when item previously did not have buy box status but now it does not, do not send message, but do write value`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, 0, false))
            )
            given(getBuyBoxStatus.invoke(productId)).willReturn(null)
            given(getLastKnownStock.invoke(productId)).willReturn(0)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(true)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(false)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(setBuyBoxStatus).invoke(productId, false)
            verifyZeroInteractions(sendNotification)
        }

    @Test
    fun `when item previously did have buy box status and stock but now it does not and stock is 0, do not send buy box message but send order message and but do write values`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            val message = "%s %s %s"
            val fullMessage = "1 $title 0"
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, 0, false))
            )
            given(getBuyBoxStatus.invoke(productId)).willReturn(true)
            given(getLastKnownStock.invoke(productId)).willReturn(1)
            given(messageProvider.getOrderMessage()).willReturn(message)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(true)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(true)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(setBuyBoxStatus).invoke(productId, false)
            verify(setLastKnownStock).invoke(productId, 0)
            verify(sendNotification, times(1)).invoke(title, fullMessage)
            verifyNoMoreInteractions(sendNotification)
        }

    @Test
    fun `when single item in stock with one lower value and buy box value is changed, send single order message and buy box message and write stock and buy box value`() =
        runBlockingTest {
            // given
            val productId = "1"
            val title = "Cheese"
            val stockAmount = 1
            val knownStockAmount = 2
            val orderMessage = "%s %s %s"
            val buyBoxMessage = "%s"
            val fullOrderMessage = "1 $title $stockAmount"
            given(getStock.invoke()).willReturn(
                listOf(Stock(productId, title, stockAmount, true))
            )
            given(getLastKnownStock.invoke(productId)).willReturn(knownStockAmount)
            given(getBuyBoxStatus.invoke(productId)).willReturn(false)
            given(messageProvider.getOrderMessage()).willReturn(orderMessage)
            given(messageProvider.getBuyBoxGainedMessage()).willReturn(buyBoxMessage)
            given(settingsRepository.isBuyBoxNotificationsEnabled()).willReturn(true)
            given(settingsRepository.isOrderNotificationsEnabled()).willReturn(true)

            // when
            val subject = createGetLVBOverview()
            subject.invoke()

            // then
            verify(setLastKnownStock).invoke(productId, stockAmount)
            verify(setBuyBoxStatus).invoke(productId, true)
            val inOrder = inOrder(sendNotification)
            inOrder.verify(sendNotification).invoke(title, fullOrderMessage)
            inOrder.verify(sendNotification).invoke(title, title)
        }

    private fun createGetLVBOverview(): CheckNewLVBState {
        return CheckNewLVBState(
            getStock,
            getLastKnownStock,
            setLastKnownStock,
            getBuyBoxStatus,
            setBuyBoxStatus,
            settingsRepository,
            messageProvider,
            sendNotification,
            TestCoroutineDispatcher()
        )
    }
}
