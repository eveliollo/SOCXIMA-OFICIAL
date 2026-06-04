package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// ==========================================
// 1. COINGECKO CRYPTO FEED DATA STRUCTURES
// ==========================================

@JsonClass(generateAdapter = true)
data class CryptoPrice(
    @Json(name = "usd") val usd: Double?,
    @Json(name = "usd_24h_change") val usd24hChange: Double?
)

@JsonClass(generateAdapter = true)
data class CoinGeckoResponse(
    @Json(name = "bitcoin") val bitcoin: CryptoPrice?,
    @Json(name = "solana") val solana: CryptoPrice?,
    @Json(name = "ethereum") val ethereum: CryptoPrice?,
    @Json(name = "dobmoney") val dobmoney: CryptoPrice?
)

// ==========================================
// 2. GEMINI REST API PROTOCOLS
// ==========================================

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: GeminiInlineData? = null
)

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "temperature") val temperature: Float? = null,
    @Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent?
)

// ==========================================
// 3. RETROFIT API DEFINTIONS
// ==========================================

interface CoinGeckoInterface {
    @GET("api/v3/simple/price")
    suspend fun getLivePrices(
        @Query("ids") ids: String = "bitcoin,solana,ethereum,dobmoney",
        @Query("vs_currencies") vsCurrencies: String = "usd",
        @Query("include_24hr_change") include24h: Boolean = true
    ): CoinGeckoResponse
}

interface GeminiInterface {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun processCommand(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// ==========================================
// 4. BITCOIN & SOLANA BLOCKCHAIN INTERFACES
// ==========================================

@JsonClass(generateAdapter = true)
data class BitcoinLatestBlock(
    @Json(name = "hash") val hash: String?,
    @Json(name = "height") val height: Long?
)

@JsonClass(generateAdapter = true)
data class SolanaRpcRequest(
    @Json(name = "jsonrpc") val jsonrpc: String = "2.0",
    @Json(name = "id") val id: Int = 1,
    @Json(name = "method") val method: String
)

@JsonClass(generateAdapter = true)
data class SolanaRpcResponse(
    @Json(name = "jsonrpc") val jsonrpc: String?,
    @Json(name = "result") val result: Long?,
    @Json(name = "id") val id: Int?
)

interface BitcoinInfoInterface {
    @GET("latestblock")
    suspend fun getLatestBlock(): BitcoinLatestBlock

    @GET("q/unconfirmedcount")
    suspend fun getUnconfirmedCount(): okhttp3.ResponseBody
}

interface SolanaRpcInterface {
    @POST("/")
    suspend fun postRpc(
        @Body request: SolanaRpcRequest
    ): SolanaRpcResponse
}

// ==========================================
// 5. NETWORK CLIENTS CONFIGURATION
// ==========================================

object NetworkClient {
    private const val COINGECKO_BASE_URL = "https://api.coingecko.com/"
    private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    private const val BITCOIN_BASE_URL = "https://blockchain.info/"
    private const val SOLANA_BASE_URL = "https://api.mainnet-beta.solana.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val coinGecko: CoinGeckoInterface by lazy {
        Retrofit.Builder()
            .baseUrl(COINGECKO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CoinGeckoInterface::class.java)
    }

    val gemini: GeminiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiInterface::class.java)
    }

    val bitcoinInfo: BitcoinInfoInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BITCOIN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BitcoinInfoInterface::class.java)
    }

    val solanaRpc: SolanaRpcInterface by lazy {
        Retrofit.Builder()
            .baseUrl(SOLANA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SolanaRpcInterface::class.java)
    }
}
