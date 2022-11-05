package com.rich.movielistapi.data.response

import com.google.gson.annotations.SerializedName

data class GetUserResponse(

	@field:SerializedName("GetUserResponse")
	val getUserResponse: List<GetUserResponseItem?>? = null
)

data class GetUserResponseItem(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
