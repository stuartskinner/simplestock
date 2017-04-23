# Simple stocks


## Super Simple Stocks

A super simple stocks simulation.

To build

    mvn package


To run

    java -jar target/simplestock-0.0.1-SNAPSHOT.jar


## Domain Assumptions

* Assume that the term ticker price in the specification is equivalent to the last derived stock price.

* Assume that lastDividend for a common stock is an input into the system.

* Assume that the term dividend in the P/E ratio is equivalent to lastDividend for common shares and fixedDividend.parValue for prefered shares.

* Assume that where a stock hasn't traded in an interval the stocks price remains unchanged.

* As such assume that the GBCE index takes into account all stocks not just those that have traded in an interval but does not include any stocks that have a price of 0

* Assume that the stock prices are calculated on the basis of a sliding window of 15 minutes calculated at 1 minute intervals.

* Assume that many of my assumptions will be wrong but thats life!!!!

## Design Constraints and Assumptions

* Assume that the volume of trades is unbounded and likely to be the most significant challenge in scaling the solution.

* Assume that the volume of stocks is comparatively small and bounded and that growth in this will be of lesser concern.

* TradeAnalytics - the windowing approach selected assumes that trades are received into the system in order and does not take account of the impacts of out of order arrival of trades.

* Assume that this a sketch of a more complex distributed architecture required to scale out to large trade volumes as such try to maintain the service boundaries and decoupling that would be required in the more complex solution.

## Design thinking

So conceptually to me the problem decomposes into the following components.

* **TradeService** - this service has responsibility for validating and persisting (null implementation in this simplified scenario) incoming trades in the system. The TradeService allows interested parties to subscribe to a feed of incoming trades.

* **TradeAnalytics** - a subscriber to the trade feed fron TradeService this component is responsible for performing analytics on the incoming stream of trade events, ultimately in this case updating stock values via StockService but could equally provide other TradeAnalytics feeds to other services within and outwith the bounds of the system.

* **StockService** - this service has responsibility for maintaining the set of stock related information for the system, in this simplified scenario the number of stocks is small and bounded and as such the stock information is held purely in memory. The stock system allows interested parties to subscribe to a stream of stock change events

**Feed Notifications** - notifications between services should be asynchronous. This will ensure that the performance of operations that originate the notifications (and will typically be triggered by external interaction) are not blocked awaiting the processing of notifications by downstream subscribers. 

## Technology Choice

For the simplified example RxJava has been used to model the notification and subscription channel between services and to provide the real time analytics capability. This was selected as it closely models the semantics of the full distributed solution in which it would be replaced by a distributed event processing solution such as KafkaStreams. Whilst this is a simplified modelling it was felt that the unbounded and potentially high volume nature of incoming trades was the key design challenge in terms of growth of the solution as such it was felt that implementing a non real time solution that assumed full set of trades could be held and processed in memory would be short sighted. RxJava also provides some useful succinct time windowing capabilities that simplified the creation of the sliding window for recalculation of stock prices.



## Future Extension

As always time runs out so a few thoughts on icky bits to tidy up and future theoretical evolution of the system.

* Improve the trade simulation.

* Add a REST API to allow external interaction.

* Sort out rounding

* Improve the structure of TradeAnalytics to separate out the windowing behaviour and allow addition of other calculations.

* Sort out the windowing to use the trade timestamp rather than order of receipt.

* Improve the testing of the time window.

This was a learning exercise in RxJava which was a fun challenge in and of itself, will leave the assessor to determine its applicability in this case, unsure i'd approach it in the same way second time around! 


The code presented is a simplified sketch of the fully scaled solution presented below.


In essence each of the service components defined within the solution TradeService, StockService would be deployed as independent services with REST API's and fronted by a load balancing capability to allow these to scale. The analytics and notification components would be replaced with Apache Kafka and KafkaStreams. Kafka would allow a resiliant persistent and highly available messaging channel which would support partitioning of the event streams across a definable set of partitions essentially allowing the analytics capabilities to scale horizontally as trade volumes increase. 
