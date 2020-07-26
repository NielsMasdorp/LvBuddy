package com.nielsmasdorp.domain.stock

import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GetStockTest {

    @Mock
    lateinit var stockRepository: StockRepository

    @Test
    fun `stock responses should be sorted by amount`() = runBlockingTest {
        // given
        val stock = listOf(
            Stock("1", "title", 1, true),
            Stock("2", "title", 2, true),
            Stock("3", "title", 3, true),
            Stock("4", "title", 4, true)
        )
        val expected = listOf(
            Stock("4", "title", 4, true),
            Stock("3", "title", 3, true),
            Stock("2", "title", 2, true),
            Stock("1", "title", 1, true)
        )
        whenever(stockRepository.getLatestStock()).thenReturn(stock)

        // when
        val subject = GetStock(stockRepository, TestCoroutineDispatcher())
        val output = subject.invoke()

        // then
        Assert.assertEquals(expected, output)
    }

    @Test
    fun `stock responses should be sorted by product title after amount`() = runBlockingTest {
        // given
        val stock = listOf(
            Stock("1", "B", 1, true),
            Stock("2", "A", 1, true),
            Stock("3", "title", 3, true),
            Stock("4", "title", 4, true)
        )
        val expected = listOf(
            Stock("4", "title", 4, true),
            Stock("3", "title", 3, true),
            Stock("2", "A", 1, true),
            Stock("1", "B", 1, true)
        )
        whenever(stockRepository.getLatestStock()).thenReturn(stock)

        // when
        val subject = GetStock(stockRepository, TestCoroutineDispatcher())
        val output = subject.invoke()

        // then
        Assert.assertEquals(expected, output)
    }

    @Test
    fun `stock with 0 amount should be filtered out`() = runBlockingTest {
        // given
        val stock = listOf(
            Stock("1", "title", 3, true),
            Stock("2", "title", 2, true),
            Stock("3", "title", 1, true),
            Stock("4", "title", 0, true)
        )
        val expected = listOf(
            Stock("1", "title", 3, true),
            Stock("2", "title", 2, true),
            Stock("3", "title", 1, true)
        )
        whenever(stockRepository.getLatestStock()).thenReturn(stock)

        // when
        val subject = GetStock(stockRepository, TestCoroutineDispatcher())
        val output = subject.invoke()

        // then
        Assert.assertEquals(expected, output)
    }

    @Test
    fun `duplicate stock should be removed and amounts added`() = runBlockingTest {
        // given
        val stock = listOf(
            Stock("1", "title", 3, true),
            Stock("1", "title", 3, true),
            Stock("2", "title", 2, true),
            Stock("3", "title", 1, true)
        )
        val expected = listOf(
            Stock("1", "title", 6, true),
            Stock("2", "title", 2, true),
            Stock("3", "title", 1, true)
        )
        whenever(stockRepository.getLatestStock()).thenReturn(stock)

        // when
        val subject = GetStock(stockRepository, TestCoroutineDispatcher())
        val output = subject.invoke()

        // then
        Assert.assertEquals(expected, output)
    }
}