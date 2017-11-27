import org.scalatest._

abstract class Base extends FlatSpec with Matchers with
OptionValues with Inside with Inspectors
