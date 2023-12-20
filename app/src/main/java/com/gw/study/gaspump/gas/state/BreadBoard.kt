package com.gw.study.gaspump.gas.state

import com.gw.study.gaspump.gas.pump.engine.state.ReceiveEngineState
import com.gw.study.gaspump.gas.pump.engine.state.SendEngineState
import com.gw.study.gaspump.gas.pump.type.state.ReceiveGasEngineState
import com.gw.study.gaspump.gas.pump.type.state.SendGasEngineState

interface BreadBoard : ReceiveEngineState, SendEngineState, ReceiveGasEngineState, SendGasEngineState