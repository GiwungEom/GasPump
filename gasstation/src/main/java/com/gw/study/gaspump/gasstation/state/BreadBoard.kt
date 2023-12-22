package com.gw.study.gaspump.gasstation.gas.state

import com.gw.study.gaspump.gasstation.gas.pump.engine.state.ReceiveEngineState
import com.gw.study.gaspump.gasstation.gas.pump.engine.state.SendEngineState
import com.gw.study.gaspump.gasstation.gas.pump.type.state.ReceiveGasEngineState
import com.gw.study.gaspump.gasstation.gas.pump.type.state.SendGasEngineState

interface BreadBoard : ReceiveEngineState, SendEngineState, ReceiveGasEngineState, SendGasEngineState