package com.meltingb.medicare.utils

sealed class NavigationEvent {
    object HomeView: NavigationEvent()
    object HelpView: NavigationEvent()
    object SearchView: NavigationEvent()
    object MapView: NavigationEvent()
    object AddView: NavigationEvent()
}

const val MENU_HOME  = 0
const val MENU_HELP  = 1
const val MENU_SEARCH  = 2
const val MENU_MAP  = 3
const val MENU_ADD  = 4

const val TYPE_NAME = 0
const val TYPE_SEQ = 1
const val TYPE_CODE = 2

const val API_DEV_KEY = "7RzOAYDkB9qRHVsXHVLPuEAUsikSSpD4YqMjJ47VbykQRu+GF6nvvmzo6K72vQ3aIJBHN/1p2uSVEhJm7B01BA=="
const val KAKAO_REST_API_KEY = "7f926b595b08b8f8790f0db142530c6a"
const val KAKAO_NATIVE_KEY = "fe8536d1069159ea279e22fefa8060a5"
const val PREF_KEY_LAST_ID = "PREF_KEY_LAST_ID"
const val PREF_KEY_PILL_LIST = "PREF_KEY_PILL_LIST"

const val CATEGORY_CODE_HP = "HP8"
const val CATEGORY_CODE_PM = "PM9"