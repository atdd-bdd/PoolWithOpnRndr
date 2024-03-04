@file:Suppress("SpellCheckingInspection")
//   This is for playing around
//
//import org.openrndr.application
//import org.openrndr.color.ColorRGBa
//import org.openrndr.extensions.SingleScreenshot
//import org.openrndr.extra.color.presets.AZURE
//import org.openrndr.extra.color.presets.DARK_BLUE
//import org.openrndr.panel.controlManager
//import org.openrndr.panel.elements.*
//import org.openrndr.panel.style.*
//
//fun main() = application {
//    program {
//        // -- this block is for automation purposes only
//        if (System.getProperty("takeScreenshot") == "true") {
//            extend(SingleScreenshot()) {
//                this.outputFile = System.getProperty("screenshotPath")
//            }
//        }
//        val rollingResistance1 = 0.0
//        var rollingResistance2 = 0.0
//        val rollingResistance3 = 0.0
//        val rollingResistance4 = 0.0
//        val cm = controlManager {
//            styleSheet(has class_ "horizontal") {
//                paddingLeft = 10.px
//                paddingTop = 10.px
//                backgroundColor = ColorRGBa.MAGENTA
//                background = Color.RGBa(ColorRGBa.MAGENTA)
//                // ----------------------------------------------
//                // The next two lines produce a horizontal layout
//                // ----------------------------------------------
//                display = Display.FLEX
//                flexDirection = FlexDirection.Row
//                width = 100.percent
//            }
//
//
//
//            layout {
//
//
//                div("horizontal") {
//
//                    button1()
//
//                    button1()
//                    textfield {
//
//                        value = "This value"
//                        label = "Value"
//                    }
//                }
//                div("horizontal") {
//
//                       slider {
//                           style = styleSheet {
//                               // Use Color.RGBa() to convert a ColorRGBa
//                               // color (the standard color datatype)
//                               // into "CSS" format:
//                               color = Color.RGBa(ColorRGBa.AZURE)
//                               background = Color.RGBa(ColorRGBa.DARK_BLUE)
//                               // height = 60.px
//                               width = 300.px
//                           }
//                           label = "Rolling Resistance"
//                           value = rollingResistance1
//                           range = Range(1.0, 2.0)
//                       }
//
//                    rollingResistance2 =  slider1(rollingResistance2)
//                }
//                div("horizontal") {
//                    slider2(rollingResistance3)
//                    slider3(rollingResistance4)
//                }
//
//            }
//        }
//        extend(cm)
//        extend {
//            drawer.clear(0.2, 0.18, 0.16, 1.0)
//            print (rollingResistance1)
//            print("\n")
//            print (rollingResistance2)
//            print("\n")
//        }
//    }
//}
//
//private fun Div.slider3(rollingResistance: Double) {
//    slider {
//        style = styleSheet {
//            // Use Color.RGBa() to convert a ColorRGBa
//            // color (the standard color datatype)
//            // into "CSS" format:
//            color = Color.RGBa(ColorRGBa.AZURE)
//            background = Color.RGBa(ColorRGBa.DARK_BLUE)
//            //height = 40.px
//            width = 100.px
//        }
//        label = "Something Else "
//        value = rollingResistance
//        range = Range(1.0, 2.0)
//
//    }
//}
//
//private fun Div.slider2(rollingResistance: Double) {
//    slider {
//        style = styleSheet {
//            // Use Color.RGBa() to convert a ColorRGBa
//            // color (the standard color datatype)
//            // into "CSS" format:
//            color = Color.RGBa(ColorRGBa.AZURE)
//            background = Color.RGBa(ColorRGBa.DARK_BLUE)
//            //height = 60.px
//            width = 100.px
//        }
//        label = "Rolling Resistance"
//        value = rollingResistance
//        range = Range(1.0, 2.0)
//    }
//}
//
//private fun Div.slider1(rollingResistance: Double) : Double {
//    var thisValue = rollingResistance
//    slider {
//        style = styleSheet {
//            // Use Color.RGBa() to convert a ColorRGBa
//            // color (the standard color datatype)
//            // into "CSS" format:
//            color = Color.RGBa(ColorRGBa.AZURE)
//            background = Color.RGBa(ColorRGBa.DARK_BLUE)
//            //height = 60.px
//            width = 100.px
//        }
//        value = thisValue
//        label = "Something Else "
//         range = Range(1.0, 2.0)
//
//        events.valueChanged.listen { thisValue = it.newValue }
//    }
//    return thisValue
//}
//
//@Suppress("unused")
//private fun Div.textField() {
//    textfield {
//        style = styleSheet {
//            background = Color.RGBa(ColorRGBa.WHITE)
//            height = 40.px
//            width = 100.px
//            color = Color.RGBa(ColorRGBa.CYAN)
//        }
//        value = "This value"
//        label = "Value"
//    }
//}
//
//private fun Div.button1() {
//    button {
//        label = "help"
//        style = styleSheet {
//            // Use Color.RGBa() to convert a ColorRGBa
//            // color (the standard color datatype)
//            // into "CSS" format:
//            background = Color.RGBa(ColorRGBa.GRAY)
//            // height = 40.px
//            width = 75.px
//            color = Color.RGBa(ColorRGBa.CYAN)
//        }
//    }
//}
//
