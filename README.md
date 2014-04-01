io-straight-fw
==============

straight.io - akka-persistence + spray.io
=======

straight.io Framework - ES/DDD/CQRS Implemented on Akka Persistence
* ES - Event Sourcing
* DDD - Domain Driven design
* CQRS - Command / Query Responsibility Separation

### Application Design

This library is an implementation of types, traits and some additional helper classes to use as the base for your application.

### Validation

Validation is expected to be used, currently the framework has two implementations
* Scalaz Validation 
* Either Validation

A later version will endeavour to shift these out as pluggable traits. Currently they are your two choices.

### Code Base

In essence there are three parts that this library implements for you.
* Event Sourced Processor trait
* Repository for storing your objects (after the events have created them)
* Service class that gives you read access to the repository for querying it

Event Sourced objects need a good key ID sequence. As a result, we use a UUID (GUUID) but not the way you normally expect.
* UuidRepository implementation

### Samples

The samples include 
* Reading Events to create some other view
* Managing Domain Validation (DDD style)
* Web implementation (see below)

#### Web APIs

This framework does not provide much in the way of webby stuff, it doesn't need to.
Rather, it provides a sample app built witho

* Play (2.2.2)
* spray.io (1.3.1)

Both Samples use AngularJS for the UI

In these two apps you will see how Event Sourcing comes to life in reality (not just some theory).

The API for the sample application is simple REST-esq services. We say REST-esq because Event Sourced / CQRS systems are all about
sending Commands, and not CRUD/PUT, DELETE, GET instructions. 

Forget the theory, forget the fanfare, the APIs you can build with this library are nice 
* REST on the Read.. (GET /person/{id}) and on the)
* REST on the write  (POST /person/changeName/{id})

#### Marshalling Commands, Events and Domain Objects 

To get the Command messages in and the Domain Objects out, we have a good implementation of Jackson JSON marshalling.
it does support Jaxb, though this is an exercise for the reader. (Ramon Buckland does have experience if you need a hand)

http://krasserm.blogspot.co.uk/2012/02/using-jaxb-for-xml-and-json-apis-in.html


### Versions

* spray.io : 1.3.1
* akka-persistence : 2.3.0
* akka : 2.3.0
* Scala : 2.10.3
* Optional : Scalaz 7.0.5 for Validation

## Issues and Feature Requests

- See the github issues list

## Details of the Implementation

### Messages
We took the approach that we don't won't this framework bleeding into your code. With that in mind,
messages do not have to extend anything, but rather just have to provide a timestamp: Long method.
```scala
sealed abstract class ConnectGroupCommand(val timestamp: Long)

...
case class ChangeConnectGroupName(
    @JsonIgnore override val timestamp: Long = DateTime.now,
    uuid : Uuid,
    @JsonDeserialize(contentAs = classOf[java.lang.Long]) expectedVersion: Option[Long], 
    name: String) extends ConnectGroupCommand(timestamp)
```

Our suggestion is to have one base Command and Event abstract class per Event Sourced Processor.
This way you can accruately route and manage these events throughout the system.

### Using a repository (your memory Image)
The repository is a memory image.

```scala
/**
 * The Service in injected with effectively Read-Only access to the memory Image
 *
 * If you have special "ways" to interrogate your Service, then put them here
 *
 * @author rbuckland
 */
class ConnectGroupService()
                         (implicit val bindingModule: BindingModule)
  extends UuidAbstractService[ConnectGroup]
  with AutoInjectable {

  val repository: UuidRepository[ConnectGroup] = inject[UuidRepository[ConnectGroup]]

}
```

## The Actor akka-persistence Processor
```scala
/**
 * ConnectGroupProcessor
 */
class ConnectGroupProcessor()
                           (implicit val bindingModule: BindingModule)
  extends SZValidationUuidAbstractProcessor[ConnectGroup, ConnectGroupEvent, ConnectGroupCommand]
  with AutoInjectable {

  val repository = inject[UuidRepository[ConnectGroup]]
  val personService = inject[PersonService]
  val idGenerator = inject[UuidGenerator[ConnectGroup]]

  /**
   * This magic method will create a ConnectGroup given an Event.
   * The AbstractProcessor calls this method on our behalf inside the persist(event) {  domainObjectFromEvent(event) }
   *
   * remember, although the event is "past tense" (eg ConnectGroupCreated) the order is
   *
   * cmd --> event --> objectCreated  .. and not
   * cmd --> objectCreated --> event
   *
   * If you look at the "ConnectGroupProcessor" as a Black Box.. a Command went in and an Event Came out
   * So .. the past Tense is for the external "looker" of this Processor, not so much us.
   *
   * We just need to know how to create a ConnectGroup, given the event (because we are event sourced)
   *
   * @param event
   * @return
   */
  override def domainObjectFromEvent(event: ConnectGroupEvent): ConnectGroup = {
    case evt: ConnectGroupCreated => ConnectGroup(evt)
    case evt: ConnectGroupNameChanged => repository.getByKey(evt.id).get.changeName(evt.name)
  }
  /**
   *
   * @return list of people
   */
  def getLeaders(leaders: List[Uuid]): SZDomainValidation[List[Person]] =
    (for (
      uuid <- leaders;
      person <- personService.get(uuid)
    ) yield person).toList.success


  // command to event
  val processCommand = process {
    case cmd @ CreateConnectGroup(timestamp, leaders, connectGroupName) => {
      for {
        leaders <- getLeaders(leaders)
      } yield ConnectGroup.canCreate(idGenerator.newId, cmd, leaders)
    }

    case cmd @ ChangeConnectGroupName(timestamp, connectGroupId, expectedVersion, newName) => {
      for {
        obj <- ensureVersion(connectGroupId, cmd.expectedVersion)
        event <- ConnectGroup.canChangeName(obj, cmd)
      } yield event
    }

  }

}

```
