package com.mohanjp.todocompose.domain.util

sealed class OrderType {
    data object Ascending: OrderType()
    data object Descending: OrderType()
}