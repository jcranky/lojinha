
import com.google.inject.AbstractModule
import main.Init

class Module extends AbstractModule {

  override def configure(): Unit =
    bind(classOf[Init]).asEagerSingleton()
}
