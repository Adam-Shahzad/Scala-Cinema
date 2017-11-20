import org.scalatest._
import org.scalatest.mockito.MockitoSugar

abstract class Base extends FlatSpec with Matchers with
OptionValues with Inside with Inspectors with MockitoSugar
