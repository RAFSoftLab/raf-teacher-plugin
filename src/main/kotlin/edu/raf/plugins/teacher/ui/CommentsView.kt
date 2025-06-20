package edu.raf.plugins.teacher.ui

import java.awt.*
import javax.swing.*
import java.io.File

class CommentsView : JPanel() {
    private val vBox = Box(BoxLayout.Y_AXIS)

    init {
        setSize(400, 400)
        layout = BorderLayout()

        // Load entities from file and create HBoxes dynamically
        val entities = listOf("Entity 1", "Entity 2", "Entity 3") // Replace with actual file path
        entities.forEach { entity ->
            val hBox = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                add(JLabel(entity))
            }
            vBox.add(hBox)
        }

        // Add VBox to the frame
        add(JScrollPane(vBox), BorderLayout.CENTER)
    }


}