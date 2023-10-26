package com.gw.study.gaspump.gas

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

class GasPump(
    private val gas: Gas,
    private val engine: PumpEngine,
    fProcess: Flow<Pair<Gas, Process>>,
    private val cScope: CoroutineScope = CoroutineScope(CoroutineName("GasPump"))
) {

    init {
        println("GasPump $this")
        cScope.launch {
            fProcess.collect { process ->
                if (process.first == gas) {
                    when (process.second) {
                        Process.Start -> engine.start()
                        Process.Pause -> engine.pause()
                        Process.Stop -> engine.destroy()
                        Process.Approach -> engine.speed = PumpEngine.Speed.Slow
                        Process.Destroy -> destroy()
                        else -> Unit
                    }
                }
            }
        }
    }

    operator fun invoke(): Flow<Gas> = engine().map { gas }

    private fun destroy() {
        cScope.coroutineContext.job.children.forEach { it.cancel() }
    }
}
