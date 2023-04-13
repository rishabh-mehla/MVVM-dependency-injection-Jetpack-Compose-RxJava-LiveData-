package com.dummy.app



object AuthUtil {

    val users = listOf("Peter", "John", "Smith")

    fun signUp(
        image:  String,
        name: String
    ): Boolean{
        return users.contains(name)
    }

}

class TestClass{

    @Test
    fun `signUp function returns false when username or password is empty`(){
        val userName = ""
        val password = ""
        val repeatPassword = ""
        assertThat(AuthUtil.signUp(userName, password,repeatPassword)).isFalse()
    }

}