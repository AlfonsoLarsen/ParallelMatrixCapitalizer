import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._



  // Define the message types
  case class CapitalizeStrings(matrix: Array[Array[String]], rowStart: Int, rowEnd: Int)

  // Generate the random string matrix
  val matrix = Array.fill(20, 20)(scala.util.Random.alphanumeric.take(5).mkString)

  // Clone the original matrix
  val originalMatrix = Array.tabulate(matrix.length, matrix(0).length)((i, j) => matrix(i)(j))

  // Divide the rows of the matrix among the available processors
  val numProcessors = Runtime.getRuntime.availableProcessors()
  val rowsPerProcessor = matrix.length / numProcessors

  // Define a function to capitalize the strings in a given range of rows
  def capitalizeStrings(matrix: Array[Array[String]], rowStart: Int, rowEnd: Int): Array[Array[String]] = {
    val result = matrix.clone()
    for (i <- rowStart until rowEnd; j <- 0 until matrix(i).length) {
      result(i)(j) = matrix(i)(j).capitalize
    }
    result
  }

  // Create a Future for each range of rows to capitalize
  val futures = (0 until numProcessors).map { i =>
    val rowStart = i * rowsPerProcessor
    val rowEnd = if (i == numProcessors - 1) matrix(rowStart).length else (i + 1) * rowsPerProcessor
    Future(capitalizeStrings(matrix, rowStart, rowEnd))
  }

  // Wait for all the futures to complete and merge the results
  val mergedMatrixSeq = Await.result(Future.sequence(futures), Duration.Inf)
  val mergedMatrix = mergedMatrixSeq.toArray.flatten

  // Print the original matrix for comparison
  println("Original matrix:")
  originalMatrix.foreach(row => println(row.mkString(" ")))

  // Print the capitalized matrix
  matrix.foreach(row => println(row.mkString(" ")))


