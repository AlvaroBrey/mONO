package com.ontherunvaro.onoclient.util

/**
 * Created by varo on 13/11/16.
 */

enum class JavascriptFunctions(val function: String, val argn: Int) {

    INSERT_USERNAME("document.getElementsByClassName('input_customer_area_login_username_field')[0].value='%s';", 1),
    INSERT_PASSWORD("document.getElementsByClassName('input_customer_area_login_password_field')[0].value='%s';", 1),
    PRESS_LOGIN_BUTTON("document.getElementById('loginBtn_button').click();", 0)
}
