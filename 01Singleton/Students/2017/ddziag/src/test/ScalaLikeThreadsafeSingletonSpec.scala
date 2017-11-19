package singleton

import java.util.concurrent.{ExecutorService, Executors}

import org.scalatest.FunSpec

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class ScalaLikeThreadsafeSingletonSpec extends FunSpec {

  class SafeThread() extends Runnable {
    var hc: Int = 0

    override def run(): Unit = {
      val ins = ScalaLikeThreadsafeSingleton.instance
      val r: Int = Random.nextInt(100)
      SafeThread.instances += ins
      hc = ins.hashCode()
    }
  }

  object SafeThread {
    val instances = new mutable.HashSet[ScalaLikeThreadsafeSingleton].empty
  }

  describe("ScalaLikeThreadsafeSingleton") {
    it("threads hashcodes should match singletons one and there should be only one instance") {
      val pool: ExecutorService = Executors.newFixedThreadPool(100)
      val pool2: ExecutorService = Executors.newFixedThreadPool(100)
      val hashCode: Int = ScalaLikeThreadsafeSingleton.instance.hashCode()
      val threadList = ArrayBuffer.empty[SafeThread]
      for (i <- 1 to 1000) {
        val sf = new SafeThread()
        threadList += sf
        pool.execute(sf)
        pool2.execute(sf)
      }
      threadList.foreach(p => {
        assert(p.hc == hashCode)
      })
      assert(SafeThread.instances.size == 1)
    }
  }
}
