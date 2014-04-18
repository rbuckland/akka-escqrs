package io.straight.fw.support

import org.mockito.Mockito
import org.mockito.verification.VerificationMode
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpecLike
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar

trait BaseSpec extends FlatSpecLike
  with ShouldMatchers
  with BeforeAndAfterAll    
  with MockitoSugar
  with MockitoWrapper {
  
}

trait MockitoWrapper {
  def spy[T](mock: T) = Mockito.spy(mock)
  def verify[T](mock: T) = Mockito.verify(mock)
  def verify[T](mock: T, mode: VerificationMode) = Mockito.verify(mock, mode)
  def when[T](methodCall: T) = Mockito.when(methodCall)
  def never = Mockito.never
  def times(wantedNumberOfInvocations: Int) = Mockito.times(wantedNumberOfInvocations)
  def reset[T](mock: T) = Mockito.reset(mock)
}
