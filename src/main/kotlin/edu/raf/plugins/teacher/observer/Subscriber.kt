package edu.raf.plugins.teacher.observer

interface Subscriber {
    fun update(data: Any)
}