package edu.raf.plugins.teacher.observer

interface Publisher {
    //Test
    fun addSubscriber(subscriber: Subscriber)
    fun removeSubscriber(subscriber: Subscriber)
    fun notifySubscribers(data: Any)
}