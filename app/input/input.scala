import model.Section

package object input {

  import scala.io

  def readSections(): Seq[model.Section] = {
    val bufferedSource = io.Source.fromFile("sections.csv")
    val sections = bufferedSource.getLines().map(_.split(",").map(_.trim))
      .map(c ⇒ Section(c(0), c(1).toInt)).toList
    //    val sections = for (line <- bufferedSource.getLines) {
    //      val cols = line.split(",").map(c ⇒ c.trim)
    //      Section(cols(0), cols(1).toInt)
    //    }
    bufferedSource.close
    sections
  }

  def readTrains(): Seq[model.Section] = {
    val bufferedSource = io.Source.fromFile("trains.csv")
    val sections = bufferedSource.getLines().map(_.split(",").map(_.trim))
      .map(c ⇒ Section(c(0), c(1).toInt)).toList
    //    val sections = for (line <- bufferedSource.getLines) {
    //      val cols = line.split(",").map(c ⇒ c.trim)
    //      Section(cols(0), cols(1).toInt)
    //    }
    bufferedSource.close
    sections
  }
}

