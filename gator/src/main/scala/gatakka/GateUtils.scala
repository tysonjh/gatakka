package gatakka

import gate.{SimpleAnnotation, AnnotationSet}
import collection.JavaConversions._

object GateUtils {
  implicit def annotationSet2list(annot: AnnotationSet): List[SimpleAnnotation] = {
    annot.size() match {
      case n if (n > 0) =>
        val it = annot.iterator()
        it.toList
      case _ =>
        List.empty[SimpleAnnotation]
    }
  }

  def annotationListprettyPrint(annots: List[SimpleAnnotation], in: String): String = {
    val sb = new StringBuilder()
    val format = "%n%1$6s %2$6s %3$20s %4$20s%n"
    sb.append(String.format(format, "Start", "End", "Type", "Value"))

    for (a <- annots) {
      val start = a.getStartNode.getOffset
      val end = a.getEndNode.getOffset
      sb.append(
        String.format(format,
                      start,
                      end,
                      a.getType,
                      in.substring(start.toInt, end.toInt)))
    }
    sb.mkString
  }
}
