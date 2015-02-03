package io.straight.escqrs.service

import akka.contrib.pattern.ShardRegion._

/*
 * Provide support for Akka cluster Sharding.  This is useful when you need to distribute 
 * actors, AggregateRoot in a DDD, across several nodes in the cluster and want to be able 
 * to interact with them using their logical identifier, but without having to care about 
 * their physical location in the cluster, which might also change over time.
 * 
 * Additional sharding related functionality can be defined in here.
 *
 * @author tsindot 
 */
trait ShardSupport {

  // implement these in the concrete processor that represents an AggregateRoot or Actor
  val idExtractor: IdExtractor
  val shardResolver: ShardResolver
  
}
