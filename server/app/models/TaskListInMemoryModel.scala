package models

import collection.mutable

object TaskListInMemoryModel {
    private val users = mutable.Map[String, String]("Olivia" -> "pass")
    private val tasks = mutable.Map[String, List[String]]("Olivia" -> List("Hello", "Goodbye"))

    def validateUser(username: String, password: String): Boolean = {
        users.get(username).map( _ == password).getOrElse(false) //more robust way
    }

    def createUser(username: String, password: String): Boolean = {
        if(users.contains(username)) false else {
            users(username) = password
            true
        }
    }

    def getTasks(username: String): Seq[String] = {
        tasks.get(username).getOrElse(Nil)
    }

    def addTask(username: String, task: String): Unit = {
        tasks(username) = task :: tasks.get(username).getOrElse(Nil)
    }

    def removeTask(username: String, index: Int): Boolean = {
        //does not have tasks to remove
        if(index < 0 || tasks.get(username).isEmpty || index >= tasks(username).length) false
        //has tasks to remove
        else {
            tasks(username) = tasks(username).patch(index, Nil, 1)
            true
        }
    }
}
