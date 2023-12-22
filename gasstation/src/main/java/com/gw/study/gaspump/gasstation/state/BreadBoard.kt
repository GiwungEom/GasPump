package com.gw.study.gaspump.gasstation.state

import com.gw.study.gaspump.gasstation.pump.engine.state.ReceiveEngineState
import com.gw.study.gaspump.gasstation.pump.engine.state.SendEngineState
import com.gw.study.gaspump.gasstation.pump.type.state.ReceiveGasEngineState
import com.gw.study.gaspump.gasstation.pump.type.state.SendGasEngineState

interface BreadBoard : ReceiveEngineState, SendEngineState, ReceiveGasEngineState, SendGasEngineState