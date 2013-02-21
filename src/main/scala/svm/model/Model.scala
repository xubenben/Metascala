package svm.model

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree._

import org.objectweb.asm
import collection.mutable

object InnerClass {
  def read(icn: InnerClassNode) = InnerClass(
    icn.name,
    icn.outerName,
    icn.innerName,
    icn.access
  )
}

case class InnerClass(name: String,
                      outerName: String,
                      innerName: String,
                      access: Int)

object LocalVariable {
  def read(lvn: LocalVariableNode) = LocalVariable(
    lvn.name,
    lvn.desc,
    lvn.signature,
    lvn.start,
    lvn.end,
    lvn.index
  )
}

case class LocalVariable(name: String,
                         desc: String,
                         signature: String,
                         start: LabelNode,
                         end: LabelNode,
                         index: Int)

object Field {
  def read(fn: FieldNode) = Field(
    fn.access,
    fn.name,
    fn.desc,
    fn.signature.safeOpt,
    fn.value,
    fn.visibleAnnotations.safeList.map(Annotation.read),
    fn.invisibleAnnotations.safeList.map(Annotation.read),
    fn.attrs.safeList.map(Attribute.read)
  )

}

case class Field(access: Int,
                 name: String,
                 desc: String,
                 signature: Option[String],
                 value: Any,
                 visibleAnnotations: List[Annotation],
                 invisibleAnnotations: List[Annotation],
                 attrs: List[Attribute])




object TryCatchBlock {
  def read(tcbn: TryCatchBlockNode) = TryCatchBlock(
    tcbn.start,
    tcbn.end,
    tcbn.handler,
    tcbn.`type`
  )
}

case class TryCatchBlock(start: LabelNode,
                         end: LabelNode,
                         handler: LabelNode,
                         blockType: String)

object Attribute {
  def read(a: asm.Attribute) = Attribute(a.`type`)
}

case class Attribute(atype: String)

object Annotation {
  def read(an: AnnotationNode) = Annotation(
    an.desc,
    an.values.safeList
  )
}

case class Annotation(desc: String,
                      values: List[Any])


object Code{
  def read(nodes: InsnList): Code = {
    val instructions = mutable.ListBuffer[Instruction]()
    val attached =
      mutable.Map.empty[Int, mutable.ListBuffer[Attached]]
                 .withDefaultValue(mutable.ListBuffer())

    for(node <- nodes.toArray){
      node match{
        case Instruction(n) => instructions.append(n)
        case Attached(a) => attached(instructions.length).append(a)
      }
    }
    Code(instructions.toList, attached.toMap.mapValues(_.toSeq))
  }
}
case class Code(instructions: Seq[Instruction],
                attached: Map[Int, Seq[Attached]])


