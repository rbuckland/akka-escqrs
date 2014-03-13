io-straight-fw
==============


straight.io - eventsourced + spray.io

# Overview

This is a simple set of helper classes for building web apps with
- akka-persistence
- spray.io
  - jackson marshalling and unmarshalling
- a neat UUID repository implementation

# TODO

- Refactor out the repository code so you can choose between an Integer based one and a UUID one
- Remove old code!
- Create a whole sample that is using the Domain architecture


# Some Samples

## Using a repository (your memory Image)
```scala
/**
 * A Service class - a wrapper around out repo to make it safe for every one
 * @param bindingModule subcut
 */
class FileService()
                 (implicit val bindingModule: BindingModule)
  extends AbstractService[StoredFile]
  with AutoInjectable {
  val repository = injectOptional [UuidWithIdRepository[StoredFile]].get
}
```

## The Actor akka-persistence Processor
```scala
/**
 *
 * @author rbuckland
 */
class FileProcessor()
                   (implicit val bindingModule: BindingModule)
  extends AbstractProcessor[StoredFile,StoredFileEvent, StoredFileCommand]
  with AutoInjectable
  with UuidGenerator[StoredFile]{

  val repository = injectOptional [UuidWithIdRepository[StoredFile]].get

  /*
   * This method is called on recovery as well as normal creation.
   * i.e This is the one to one mapping between an Event and Creation or modification
   * of a Domain Object. I guess .. tread carefully here. It must be self contained.
   */
  override def eventToDomainObject(event: StoredFileEvent):StoredFile = {
    event match {
      case e: NewFileAdded => StoredFile(e)
      case e: FileContentChanged => repository.getByKey(e.uuid).get.copy(filename = e.newFilename, changedDateTime = Some(e.datetimeChanged))
    }
  }

  def generateId = newUuid(DateTime.now.getMillis) // call UUID when we need it

  val receiveCommand: Receive = {

    case cmd : AddNewFile => process { StoredFile.canCreate(generateId,cmd) }
    case cmd : ChangeFile => process {
      for {
        obj <- ensureVersion(cmd.uuid,Option(cmd.expectedVersion))
      } yield FileContentChanged(obj.uuid,obj.displayName,cmd.newFilename,DateTime.now)
    }
  }
}
```






