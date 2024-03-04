class Debug {
    companion object {
        var debugOn = false
        fun println(value: String)
        {
            if (debugOn)
                println(value)
        }
    }

}