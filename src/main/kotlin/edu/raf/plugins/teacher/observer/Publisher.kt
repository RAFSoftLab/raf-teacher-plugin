package edu.raf.plugins.teacher.observer

interface Publisher {
    //Test fors
    fun addSubscriber(subscriber: Subscriber)
    fun removeSubscriber(subscriber: Subscriber)
    fun notifySubscribers(data: Any)
}