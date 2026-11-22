package com.example.trace.ai

import android.util.Base64
import com.example.trace.BuildConfig
import com.example.trace.model.Product
import com.example.trace.model.SourceLink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException

