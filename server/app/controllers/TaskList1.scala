package controllers

import models._
import javax.inject._ //bringing in elements for dependency injection
import play.api.mvc._
import play.api.i18n._
import play.api.data._
import play.api.data.Forms._

case class LoginData(username: String, password: String)

@Singleton 
class TaskList1 @Inject()(cc: MessagesControllerComponents) extends MessagesAbstractController(cc) { //extend Abstract controller so it actually becomes a controller
    val loginForm = Form(mapping(
        "Username" -> text(3,10),
        "Password" -> text(8)
    )(LoginData.apply)(LoginData.unapply))

    def login = Action { implicit request =>
        Ok(views.html.login1(loginForm))
    }

    def validateLoginGet(username: String, password: String) = Action {
        Ok(s"You are $username logged in with $password")
    }

    def validateLoginPost = Action { implicit request => 
        val postVals = request.body.asFormUrlEncoded
        postVals.map{args => 
            val username = args("username").head
            val password = args("password").head
            if(TaskListInMemoryModel.validateUser(username, password)){
                Redirect(routes.TaskList1.taskList).withSession("username" -> username)
            }
            else{
                Redirect(routes.TaskList1.login).flashing("error" -> "Invalid username/password combination")
            }
            
        }.getOrElse(Redirect(routes.TaskList1.login))
    }

    def validateLoginForm = Action { implicit request =>
        loginForm.bindFromRequest().fold(
            formWithErrors => BadRequest(views.html.login1(formWithErrors)),
            ld => 
                if(TaskListInMemoryModel.validateUser(ld.username, ld.password)){
                Redirect(routes.TaskList1.taskList).withSession("username" -> ld.username)
            }
            else{
                Redirect(routes.TaskList1.login).flashing("error" -> "Invalid username/password combination")
            }
        )
    }

    def createUser = Action { implicit request => 
        val postVals = request.body.asFormUrlEncoded
        postVals.map{args => 
            val username = args("username").head
            val password = args("password").head
            if(TaskListInMemoryModel.createUser(username, password)){
                Redirect(routes.TaskList1.taskList).withSession("username" -> username)
            }
            else{
                Redirect(routes.TaskList1.login).flashing("error" -> "User creation failed")
            }
            
        }.getOrElse(Redirect(routes.TaskList1.login))

    }
    
    def taskList = Action { implicit request =>
        val usernameOption = request.session.get("username")
        usernameOption.map{username => //If they got here with a username we look up that user's tasks
            val tasks = TaskListInMemoryModel.getTasks(username)
            Ok(views.html.taskList1(tasks))
        }.getOrElse(Redirect(routes.TaskList1.login)) //If not we redirect them back to the login page
    }

    def logout = Action {
        Redirect(routes.TaskList1.login).withNewSession
    }

    def addTask = Action { implicit request =>
        val usernameOption = request.session.get("username")
        usernameOption.map { username =>
            val postVals = request.body.asFormUrlEncoded
            postVals.map { args => 
                val task = args("newTask").head
                TaskListInMemoryModel.addTask(username,task)
                Redirect(routes.TaskList1.taskList)
            }.getOrElse(Redirect(routes.TaskList1.taskList))
        }.getOrElse(Redirect(routes.TaskList1.login))
    }

    def deleteTask = Action { implicit request =>
        val usernameOption = request.session.get("username")
        usernameOption.map { username =>
            val postVals = request.body.asFormUrlEncoded
            postVals.map { args => 
                val index = args("index").head.toInt
                TaskListInMemoryModel.removeTask(username,index)
                Redirect(routes.TaskList1.taskList)
            }.getOrElse(Redirect(routes.TaskList1.taskList))
        }.getOrElse(Redirect(routes.TaskList1.login))
    }

    //In Class Group Coding Below

    def createTable = Action { implicit request =>
        val rawData = "team won lost min  fgm  fga   3m   3a  ftm  fta   or   tr   as   st   to   bk   pf   pts  tc  ej  ff AtlantaHawks         41   41 19443 3658 7574  882 2505 1513 1850  920 3639 2049  580 1013  401 1541  9711   0   0   0 BostonCeltics        57   25 19567 3460 7278 1315 3494 1436 1769  796 3717 2186  521 1043  431 1542  9671   0   0   0 CharlotteHornets     27   55 19440 3385 7413  881 2669 1447 1933  901 3649 2062  633 1118  425 1661  9098   0   0   0"
        val splitData = rawData.split(" +");
        val numCols = 21
        val numRows = 4
        val twoDArray:Array[Array[String]] = Array.ofDim(numRows, numCols)
        for(i <- 0 to (numRows-1)){
            for(j <- 0 to (numCols-1)){
                twoDArray(i)(j) = splitData(numCols*i + j)
            }
        }
        Ok(views.html.renderTable(twoDArray))

    }

    def favoriteColor = Action { implicit request =>
        Ok(views.html.userFavorites())
    }

    def displayFavorite(name: String, color: String) = Action { implicit request =>
        Ok(s"$name's favorite color is $color")
    }

    def classLogin = Action { implicit request =>
        Ok(views.html.classLogin())
    }

    def displayClassLogin = Action { implicit request =>
        val postVals = request.body.asFormUrlEncoded
        postVals.map{args => 
            val username = args("username").head
            val password = args("password").head
            Ok(s"$username's password is $password")
            
        }.getOrElse(Redirect(routes.TaskList1.classLogin))
    }
}
