package org.dit113group3.androidapp

enum class Direction {
    UP {
        override fun toString(): String {
            return "N"
        }
    },
    RIGHT {
        override fun toString(): String {
            return "E"
        }
    },
    LEFT {
        override fun toString(): String {
            return "W"
        }
    },
    DOWN {
        override fun toString(): String {
            return "S"
        }
    },
    UP_RIGHT {
        override fun toString(): String {
            return "NE"
        }
    },
    UP_LEFT {
        override fun toString(): String {
            return "NW"
        }
    },
    DOWN_RIGHT {
        override fun toString(): String {
            return "SE"
        }
    },
    DOWN_LEFT {
        override fun toString(): String {
            return "SW"
        }
    }
}