# LvBuddy

![Alt text](/screenshots/screenshot2.png?raw=true "Voorraad")
![Alt text](/screenshots/screenshot1.png?raw=true "Instellingen")

## Functionaliteit:

- Zie je huidige LVB voorraad
- Zie per product in je LVB voorraad of jij het koopblok hebt (te zien aan de gouden medaille)
- Stuurt notificaties als je voorraad/koopblok status verandert
- Optie om notificaties naar een Telegram (groeps)chat te sturen
- Je Bol.com credentials worden versleuteld opgeslagen op je telefoon en verlaten je telefoon nooit
- Dark mode
- Kopieer snel een EAN door lang op een product te drukken
- Zie de volledige product naam door op een product te drukken

## Benodigdheden voor het gebruik van deze app (tonen LVB voorraad):

- Android apparaat met min. Android 8.0
- Bol.com Retailer V3 client id
- Bol.com Retailer V3 client secret

Ga naar je winkel instellingen in jouw Bol.com account, kies vervolgens API Instellingen (onder "Diensten") en maak je credentials aan.

## Benodigdheden voor het laten zien van je koopblok status

- Winkel naam (moet precies overeenkomen)

## Benodigdheden voor het sturen van notificaties in Telegram

In plaats van systeem notificaties kan LvBuddy ook notificaties in een Telegram chat naar keuze sturen, dit is bijvoorbeeld
handig als je met meerdere mensen het zelfde bol.com verkopers account beheert en wilt dat iedereen updates krijgt. 
Je hebt hiervoor nodig:

- Telegram bot token
- Telegram chat id

Om deze informatie te verkrijgen: open Telegram en start een gesprek met `@BotFather` om een nieuwe Telegram bot te maken.
Zodra deze gemaakt is (geef hem een naam en eventueel een plaatje) krijg je een token, dit is het token van jouw bot.
Disable de privacy settings van je bot door het `/setprivacy` command naar `@BotFather` te sturen.
Hierdoor kan jouw bot berichten lezen in de chats waar jij hem in toevoegt.

Maak een nieuwe groepschat aan op Telegram en voeg je bot toe met de naam die je de bot gegeven hebt.
Ga vervolgens naar de instellingen van de chat (klik op de naam) en maak je bot administrator in de chat.
Stuur zelf een bericht in de chat, je kunt vervolgens achter je chat id komen door in je browser de volgende URL aan te roepen: `https://api.telegram.org/{botToken}/getUpdates`,
vervang hierbij `{botToken}` met je eigen token.
Als het goed is zie je dan je eigen bericht voorbij komen en daar kun je ook het id van de chat vinden:

```
{
    "update_id": 000000,
    "message": {
        "message_id": 00000,
        "from": {
            "id": 000000,
            "is_bot": false,
            "first_name": "Jouw naam",
            "username": "-----"
        },
        "chat": {
            "id": {dit id moet je hebben},
            "title": "Groepnaam",
            "type": "supergroup"
        },
        "date": 1595505653,
        "text": "jouw bericht"
    }
}
```

Mocht je je eigen chat nog niet zien, soms duurt het wat langer voordat hij in het lijstje staat, probeer het dan later opnieuw.

Je hebt nu je bot token en chat id, deze kun je in de instellingen van LvBuddy invullen.

# Bekende problemen

- Bij het terughalen van voorraad zal de LvBuddy voorraad notificaties versturen omdat deze minder wordt
- Vanwege limieten op de Bol.com API kun je maximaal 1000 producten in je assortiment hebben (inclusief LVB producten zonder voorraad)
- Hoe meer producten hoe langer het duurt om de informatie in te laden als je de koopblok check aan hebt staan omdat er per product een HTML pagina opgehaald wordt.

Het zou kunnen dat jouw type telefoon/OS het achtergrond werk wat LvBuddy doet stopt om batterij te besparen. 
Bijvoorbeeld telefoons van Xiaomi hebben hier last van, om dit op te lossen kun je informatie vinden op https://dontkillmyapp.com/

# Disclaimer

Het bepalen of je het koopblok hebt is niet officieel ondersteund door Bol.com en er is geen hierdoor geen garantie dat dit blijft werken en/of dat Bol.com het er mee eens is.
Het gebruik van LvBuddy is volledig op eigen risico, ik ben niet verantwoordelijk voor geleden schade aan je bol.com business door het gebruik van LvBuddy.

# Licentie

```
Copyright 2020 Niels Masdorp
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```