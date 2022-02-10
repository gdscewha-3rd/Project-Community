package com.example.leafy2.cardnews

import com.example.leafy2.R

class DataSource {
    fun loadThumbnail(): List<ThumbnailData> {
        return listOf(
            ThumbnailData(R.drawable.thumb_01, "식물이 좋아하는 물 주기 방법을 알아보아요~"),
            ThumbnailData(R.drawable.thumb_02, "다육이를 위해 영양분 넘치는 새 흙으로 분갈이 해봐요!"),
            ThumbnailData(R.drawable.thumb_04, "잎꽂이 꿀팁"),
            ThumbnailData(R.drawable.thumb_03, "햇빛이 부족하다면?! 인조조명!")
        )
    }

    fun loadContents(pos: Int): List<ContentsData> {
        return when (pos) {
            0 -> listOf(
                ContentsData(R.drawable.cardnews01_1),
                ContentsData(R.drawable.cardnews01_2),
                ContentsData(R.drawable.cardnews01_3),
                ContentsData(R.drawable.cardnews01_4),
                ContentsData(R.drawable.cardnews01_5)
            )
            else -> listOf(
                ContentsData(R.drawable.cardnews02_1),
                ContentsData(R.drawable.cardnews02_2),
                ContentsData(R.drawable.cardnews02_3),
                ContentsData(R.drawable.cardnews02_4),
                ContentsData(R.drawable.cardnews02_5)
            )

        }

    }
}