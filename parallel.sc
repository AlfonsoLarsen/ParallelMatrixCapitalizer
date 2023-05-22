import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

// Generate the random string matrix
val matrix = Array.fill(20, 20)(scala.util.Random.alphanumeric.take(5).mkString)

// Clone the original matrix
val originalMatrix = Array.tabulate(matrix.length, matrix(0).length)((i, j) => matrix(i)(j))

// Divide the rows of the matrix among the available processors
val numProcessors = Runtime.getRuntime.availableProcessors()
val rowsPerProcessor = (matrix.length.toDouble / numProcessors.toDouble).ceil.toInt

// Define a function to encode the strings in a given range of rows using ASCII values
def encodeStrings(matrix: Array[Array[String]]): Array[Array[String]] = {
  matrix.map { row =>
    row.map { str =>
      val encodedString = str.map(c => (c.toInt + 1).toChar) // Encode using ASCII values
      encodedString
    }
  }
}

// Create a Future for each group of rows to encode
val futures = matrix.grouped(rowsPerProcessor).map { group =>
  Future(encodeStrings(group))
}

// Wait for all the futures to complete and merge the results
val mergedMatrixSeq = Await.result(Future.sequence(futures), Duration.Inf)
val mergedMatrix = mergedMatrixSeq.flatten.map(_.mkString(" "))

// Print the original matrix for comparison
println("Original matrix:")
originalMatrix.foreach(row => println(row.mkString(" ")))

// Print the encoded matrix
println("Encoded matrix:")
mergedMatrix.foreach(row => println(row))


