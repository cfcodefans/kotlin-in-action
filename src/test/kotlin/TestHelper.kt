import org.slf4j.Logger
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

fun Logger.probe() {
    this.info(Thread.currentThread().stackTrace.joinToString("\n\t", "{", "}"))
}

fun Logger.probe(layer: Int) {
    this.info(Thread.currentThread().stackTrace.slice(0..layer)
            .joinToString("\n\t", "{", "}"))
}

fun <T : Any?> Class<T>.info(all: Boolean = false): String {
    val cls = this
    val modifierStr: String = Modifier.toString(cls.modifiers)
    val clsName: String = " ${cls.enclosingClass?.simpleName.orEmpty()}$${cls.simpleName} "
    val clsOrInterface: String = if (Modifier.isInterface(cls.modifiers)) "interface" else "class"
    val superCls: String = if (Modifier.isInterface(cls.modifiers)) "extends ${cls.superclass.simpleName}" else ""
    val interfaceStr: String = if (cls.interfaces.isNotEmpty())
        "implements ${cls.interfaces.map { it.simpleName }.joinToString(",")} "
    else
        ""

    val fieldsStr = cls.declaredFields
            .map { " ${Modifier.toString(it.modifiers)} ${it.type.simpleName} ${it.name} " }
            .joinToString("\n\t", "\t//field start\n", "\n\t//field end\n")

    fun paramsStr(md: Method): String = md.parameters
            .map { "${it.type.simpleName} ${md.name}" }
            .joinToString(", ")

    val methodsStr = (if (all) cls.methods else cls.declaredMethods)
            .map { " ${Modifier.toString(it.modifiers)} ${it.returnType.simpleName} ${it.name}(${paramsStr(it)})" }
            .joinToString("\n\t", "\t//method start\n\t", "\t//method end\n")

    return """\n
$modifierStr $clsOrInterface $clsName $superCls $interfaceStr {
    $fieldsStr
    $methodsStr
}
    """.trimIndent()
}

fun <T : Any?> Logger.inspect(cls: Class<T>, all: Boolean = false) {
    this.info(cls.info(all))
}

fun Logger.info(v: Any?) = this.info("{}", v)