/*
 * mONO is a free app for a telephony provider's client area.
 * Copyright (C) 2017 Álvaro Brey Vilas <alvaro.brv@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0>.
 */

package com.ontherunvaro.onoclient.util

/**
 * Created by varo on 5/11/16.
 */

object OnoURL {

    val BASE_URL = "ono.es"
    private val SEPARATOR = "/"

    fun builder(): OnoURLBuilder {
        return OnoURLBuilder()
    }

    enum class OnoPage(internal val value: String) {

        CLIENT_AREA("clientes"), LOGIN("area-cliente/login");

        override fun toString(): String {
            return value
        }
    }

    class OnoURLBuilder internal constructor() {

        private val current: StringBuilder = StringBuilder("https://" + BASE_URL)

        fun withPage(page: OnoPage): OnoURLBuilder {
            current.append(SEPARATOR)
            current.append(page.value)
            return this
        }

        fun withString(value: String): OnoURLBuilder {
            current.append(SEPARATOR)
            current.append(value)
            return this
        }

        override fun toString(): String {
            return current.toString()
        }
    }
}
