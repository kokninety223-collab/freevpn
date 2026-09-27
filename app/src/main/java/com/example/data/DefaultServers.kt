package com.example.data

import com.example.model.ServerRegion
import com.example.model.VpnProtocol
import com.example.model.VpnServer

object DefaultServers {
    val servers: List<VpnServer> = listOf(
        // --- ASIA SERVERS ---
        VpnServer(
            id = "asia_sg_1",
            name = "Singapore - Marina Bay",
            country = "Singapore",
            countryCode = "SG",
            city = "Singapore",
            region = ServerRegion.ASIA,
            ip = "139.180.208.41",
            pingMs = 24,
            loadPercentage = 34,
            flagEmoji = "🇸🇬",
            protocol = VpnProtocol.WIREGUARD,
            isRecommended = true
        ),
        VpnServer(
            id = "asia_sg_2",
            name = "Singapore - Changi Gaming",
            country = "Singapore",
            countryCode = "SG",
            city = "Singapore",
            region = ServerRegion.ASIA,
            ip = "128.199.198.77",
            pingMs = 28,
            loadPercentage = 42,
            flagEmoji = "🇸🇬",
            protocol = VpnProtocol.OPENVPN_UDP
        ),
        VpnServer(
            id = "asia_mm_1",
            name = "Myanmar - Yangon Relay",
            country = "Myanmar",
            countryCode = "MM",
            city = "Yangon",
            region = ServerRegion.ASIA,
            ip = "103.116.14.88",
            pingMs = 18,
            loadPercentage = 29,
            flagEmoji = "🇲🇲",
            protocol = VpnProtocol.WIREGUARD,
            isRecommended = true
        ),
        VpnServer(
            id = "asia_jp_1",
            name = "Japan - Tokyo Akihabara",
            country = "Japan",
            countryCode = "JP",
            city = "Tokyo",
            region = ServerRegion.ASIA,
            ip = "172.104.103.54",
            pingMs = 45,
            loadPercentage = 48,
            flagEmoji = "🇯🇵",
            protocol = VpnProtocol.WIREGUARD
        ),
        VpnServer(
            id = "asia_jp_2",
            name = "Japan - Osaka Kansai",
            country = "Japan",
            countryCode = "JP",
            city = "Osaka",
            region = ServerRegion.ASIA,
            ip = "153.121.58.20",
            pingMs = 52,
            loadPercentage = 39,
            flagEmoji = "🇯🇵",
            protocol = VpnProtocol.STEALTH
        ),
        VpnServer(
            id = "asia_kr_1",
            name = "South Korea - Seoul Fast",
            country = "South Korea",
            countryCode = "KR",
            city = "Seoul",
            region = ServerRegion.ASIA,
            ip = "211.233.56.12",
            pingMs = 58,
            loadPercentage = 55,
            flagEmoji = "🇰🇷",
            protocol = VpnProtocol.WIREGUARD
        ),
        VpnServer(
            id = "asia_th_1",
            name = "Thailand - Bangkok",
            country = "Thailand",
            countryCode = "TH",
            city = "Bangkok",
            region = ServerRegion.ASIA,
            ip = "122.155.168.10",
            pingMs = 38,
            loadPercentage = 36,
            flagEmoji = "🇹🇭",
            protocol = VpnProtocol.OPENVPN_UDP
        ),
        VpnServer(
            id = "asia_my_1",
            name = "Malaysia - Kuala Lumpur",
            country = "Malaysia",
            countryCode = "MY",
            city = "Kuala Lumpur",
            region = ServerRegion.ASIA,
            ip = "175.143.12.90",
            pingMs = 32,
            loadPercentage = 40,
            flagEmoji = "🇲🇾",
            protocol = VpnProtocol.WIREGUARD
        ),
        VpnServer(
            id = "asia_tw_1",
            name = "Taiwan - Taipei Cloud",
            country = "Taiwan",
            countryCode = "TW",
            city = "Taipei",
            region = ServerRegion.ASIA,
            ip = "104.28.18.23",
            pingMs = 62,
            loadPercentage = 45,
            flagEmoji = "🇹🇼",
            protocol = VpnProtocol.WIREGUARD
        ),
        VpnServer(
            id = "asia_in_1",
            name = "India - Mumbai Speedway",
            country = "India",
            countryCode = "IN",
            city = "Mumbai",
            region = ServerRegion.ASIA,
            ip = "13.232.189.5",
            pingMs = 68,
            loadPercentage = 52,
            flagEmoji = "🇮🇳",
            protocol = VpnProtocol.OPENVPN_TCP
        ),

        // --- USA SERVERS ---
        VpnServer(
            id = "usa_la_1",
            name = "USA - Los Angeles (West)",
            country = "United States",
            countryCode = "US",
            city = "Los Angeles, CA",
            region = ServerRegion.USA,
            ip = "198.199.112.45",
            pingMs = 112,
            loadPercentage = 44,
            flagEmoji = "🇺🇸",
            protocol = VpnProtocol.WIREGUARD,
            isRecommended = true
        ),
        VpnServer(
            id = "usa_sf_1",
            name = "USA - San Francisco",
            country = "United States",
            countryCode = "US",
            city = "San Francisco, CA",
            region = ServerRegion.USA,
            ip = "143.198.170.92",
            pingMs = 118,
            loadPercentage = 38,
            flagEmoji = "🇺🇸",
            protocol = VpnProtocol.WIREGUARD
        ),
        VpnServer(
            id = "usa_sea_1",
            name = "USA - Seattle Cloud",
            country = "United States",
            countryCode = "US",
            city = "Seattle, WA",
            region = ServerRegion.USA,
            ip = "167.99.162.24",
            pingMs = 120,
            loadPercentage = 41,
            flagEmoji = "🇺🇸",
            protocol = VpnProtocol.STEALTH
        ),
        VpnServer(
            id = "usa_ny_1",
            name = "USA - New York (East)",
            country = "United States",
            countryCode = "US",
            city = "New York, NY",
            region = ServerRegion.USA,
            ip = "159.203.188.7",
            pingMs = 135,
            loadPercentage = 56,
            flagEmoji = "🇺🇸",
            protocol = VpnProtocol.WIREGUARD
        ),
        VpnServer(
            id = "usa_chi_1",
            name = "USA - Chicago Central",
            country = "United States",
            countryCode = "US",
            city = "Chicago, IL",
            region = ServerRegion.USA,
            ip = "165.227.200.11",
            pingMs = 129,
            loadPercentage = 49,
            flagEmoji = "🇺🇸",
            protocol = VpnProtocol.OPENVPN_UDP
        ),
        VpnServer(
            id = "usa_dal_1",
            name = "USA - Dallas Core",
            country = "United States",
            countryCode = "US",
            city = "Dallas, TX",
            region = ServerRegion.USA,
            ip = "104.236.240.8",
            pingMs = 131,
            loadPercentage = 46,
            flagEmoji = "🇺🇸",
            protocol = VpnProtocol.WIREGUARD
        ),
        VpnServer(
            id = "usa_mia_1",
            name = "USA - Miami South",
            country = "United States",
            countryCode = "US",
            city = "Miami, FL",
            region = ServerRegion.USA,
            ip = "138.68.225.19",
            pingMs = 142,
            loadPercentage = 50,
            flagEmoji = "🇺🇸",
            protocol = VpnProtocol.OPENVPN_TCP
        )
    )

    fun getDefaultServer(): VpnServer = findBestAsiaServer(servers)

    fun findBestAsiaServer(currentList: List<VpnServer> = servers): VpnServer {
        return currentList
            .filter { it.region == ServerRegion.ASIA }
            .minByOrNull { it.pingMs + (it.loadPercentage * 0.35f) }
            ?: servers.first { it.region == ServerRegion.ASIA }
    }

    fun findBestUsaServer(currentList: List<VpnServer> = servers): VpnServer {
        return currentList
            .filter { it.region == ServerRegion.USA }
            .minByOrNull { it.pingMs + (it.loadPercentage * 0.35f) }
            ?: servers.first { it.region == ServerRegion.USA }
    }

    fun findBestOverallServer(currentList: List<VpnServer> = servers): VpnServer {
        return currentList.minByOrNull { it.pingMs + (it.loadPercentage * 0.35f) } ?: servers.first()
    }
}
