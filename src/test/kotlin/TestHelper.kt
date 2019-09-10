import org.slf4j.Logger

fun Logger.probe() {
    this.info(Thread.currentThread().stackTrace.joinToString("\n\t", "{", "}"))
}

fun Logger.probe(layer: Int) {
    this.info(Thread.currentThread().stackTrace.slice(0..layer)
            .joinToString("\n\t", "{", "}"))
}