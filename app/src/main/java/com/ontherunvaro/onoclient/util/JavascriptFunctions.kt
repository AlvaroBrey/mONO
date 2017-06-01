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
 * Created by varo on 13/11/16.
 */

enum class JavascriptFunctions(val function: String, val argn: Int) {

    INSERT_USERNAME("document.getElementsByClassName('input_customer_area_login_username_field')[0].value='%s';", 1),
    INSERT_PASSWORD("document.getElementsByClassName('input_customer_area_login_password_field')[0].value='%s';", 1),
    PRESS_LOGIN_BUTTON("document.getElementById('loginBtn_button').click();", 0)
}
