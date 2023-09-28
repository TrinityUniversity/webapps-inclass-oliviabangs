import org.scalatestplus.play._
import models._

class TaskListInMemoryModelSpec extends PlaySpec {
    "TaskListInMemoryModel" must {
        "do valid login for default user" in {
            TaskListInMemoryModel.validateUser("Olivia", "pass") mustBe (true)
        }

        "reject login with wrong password" in {
            TaskListInMemoryModel.validateUser("Olivia", "wrong") mustBe (false)
        }

        "reject login with wrong username" in {
            TaskListInMemoryModel.validateUser("Livvie", "pass") mustBe (false)
        }

        "reject login with wrong username and password" in {
            TaskListInMemoryModel.validateUser("Livvie", "wrong") mustBe (false)
        }

        "get correct default tasks" in {
            TaskListInMemoryModel.getTasks("Olivia") mustBe (List("Hello", "Goodbye"))
        }

        "create new user with no tasks" in {
            TaskListInMemoryModel.createUser("Person", "pass") mustBe (true)
            TaskListInMemoryModel.getTasks("Person") mustBe (Nil)
        }

        "create user fails when user already exists" in {
            TaskListInMemoryModel.createUser("Olivia", "pass") mustBe (false)
        }

        "Add new task for default user" in {
            TaskListInMemoryModel.addTask("Olivia", "Example")
            TaskListInMemoryModel.getTasks("Olivia") must contain ("Example")
        }

        "add new task for new user" in {
            TaskListInMemoryModel.addTask("Person", "Example 2")
            TaskListInMemoryModel.getTasks("Person") must contain ("Example 2")            
        }

        "remove task from default user" in {
            TaskListInMemoryModel.removeTask("Olivia", TaskListInMemoryModel.getTasks("Olivia").indexOf("Hello"))
            TaskListInMemoryModel.getTasks("Olivia") must not contain ("Hello")
        }
    }
}