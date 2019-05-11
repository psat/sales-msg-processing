# sales-msg-processing
The application needs at least Java 1.8 runtime, with Maven as the dependency and build manager.
The app's main entry point just runs a simulation of messages being processed - 100 messages are generated for demonstrating
that after the 50th message they are no longer processed and also to show the generation of sales reports as well as
the adjusted values report.

## Design Considerations
During implementation tried to make the app as modular as possible. On a real environment one 
wants to easily switch modules, add/extend functionality to the system as well as to ease maintainability.
As such the app is divided into several packages for better organization.
Most classes depend either on an Interface or an Abstraction so that they are open for extension.
Most classes are trying to do one thing only which also ties in with the Segregation principle. 
One class that probably violates this principle is the AdjustSaleMessage which contains some logic for adjusting
 the value of sales - but as everything this was a trade off, as I believe that it, and only it knows if adjustment is needed
 and the operation to apply.
Visitor pattern implemented in order to avoid making a reference to a specific class when trying to process several types of 
messages. Therefore was possible to delegate the operation to the adequate message implementation.
 
## Methodologies and OO Principles 
Applied TDD for the most part. Always had the SOLID principles in mind. No comments written - method names are hopefully
clear and concise, also methods are usually small, leading to a cleaner and easier to maintain codebase.

## Build & Testing
Simply run on the console

`mvn clean install`

### Unit Tests
Using JUnit4 with AssertJ for fluent assertions
Some variables where exposed for the purpose of assertions - ideally they should not have been exposed.
To be able to do assertions on reports, and to avoid the usage of a 3rd party library (such as Mockito) created a
Spy implementation of the ReportGenerator which encapsulates a concrete ReportGenerator implementation.

## Running
After building the application, assuming at the root of projects folder, run the application as:

`java -cp target/sales-msg-processing-1.0-SNAPSHOT.jar com.psat.SalesMsgProcessingApp`
