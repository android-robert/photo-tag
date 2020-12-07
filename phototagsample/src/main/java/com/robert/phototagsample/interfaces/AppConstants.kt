package com.robert.phototagsample.interfaces

interface AppConstants {
    interface ProgressText {
        companion object {
            const val PROGRESS_MSG = "Saving Tag..."
            const val RELOADING = "Reloading..."
        }
    }

    interface Animations {
        interface Show {
            companion object {
                const val BOUNCE_DOWN = "Bounce Down"
                const val FADE_IN = "Fade In"
                const val SLIDE_DOWN = "Slide Down"
                const val ZOOM_IN = "Zoom In"
            }
        }

        interface Hide {
            companion object {
                const val BOUNCE_UP = "Bounce Up"
                const val FADE_OUT = "Fade Out"
                const val SLIDE_UP = "Slide Up"
                const val ZOOM_OUT = "Zoom Out"
            }
        }
    }

    interface ToastText {
        companion object {
            const val CHOOSE_A_PHOTO = "Please choose a photo"
            const val TAG_ONE_USER_AT_LEAST = "Please tag one user at least"
            const val PHOTO_TAGGED_SUCCESSFULLY = "Photo tagged successfully"
        }
    }

    interface PreferenceKeys {
        companion object {
            const val TAGGED_PHOTOS = "TAGGED_PHOTOS"
        }
    }

    interface Events {
        companion object {
            const val NEW_CONFIGURATION_SAVED = "NEW_CONFIGURATION_SAVED"
            const val NEW_PHOTO_IS_TAGGED = "NEW_PHOTO_IS_TAGGED"
        }
    }

    companion object {
        const val OFFSCREEN_PAGE_LIMIT = 3
        const val ADD_TAG_DELAY_MILLIS = 2000
        const val CONFIGURATION_DELAY_MILLIS = 2000
        const val CHOOSE_A_PHOTO_TO_BE_TAGGED = 5000
        const val NO_USER_FOUND = "No User Found"
    }
}