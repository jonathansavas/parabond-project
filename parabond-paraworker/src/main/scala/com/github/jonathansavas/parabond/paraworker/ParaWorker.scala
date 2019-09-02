package com.github.jonathansavas.parabond.paraworker

import com.github.jonathansavas.parabond.ParaWorker.ParaWorkerProto.{Partition, Result}
import org.apache.logging.log4j.LogManager

class ParaWorker {
  private val logger = LogManager.getLogger(classOf[ParaWorker])

  def work(partition: Partition): Result = {
    return Result.newBuilder().setPortfId(partition.getN).build();
  }
}
