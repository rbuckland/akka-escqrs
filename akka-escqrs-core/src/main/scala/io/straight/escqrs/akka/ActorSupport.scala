package io.straight.escqrs.akka

/**
 * @author rbuckland
 */
trait ActorSupport {

  def ACTOR_NAME: String = {
    //tail tail tail is to strip off the first 3 package names ( this could explode!)
    val name = this.getClass.getPackage.getName.split('.').tail.tail.tail.mkString(".") +
                   "." + this.getClass.getSimpleName.split('$')(0)
    return name

  }

  def actorPath = "/user/" + ACTOR_NAME

}
