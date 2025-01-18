package edu.raf.plugins.teacher.ui
import javax.swing.*
import java.awt.*


class GetStudentSolutionsView : JPanel() {
    private var currentStep = 0

    private val steps = arrayOf("Korak 1: Izaberite predmet", "Korak 2: Izaberite godinu", "Korak 3: Izaberite proveru znanja", "Korak 4: Izaberite grupu")
    private val progressBar: JProgressBar = JProgressBar(0, steps.size)
    private val stepLabel: JLabel = JLabel(steps[currentStep], JLabel.CENTER)
    private val nextButton: JButton = JButton("Sledeći korak")
    private val prevButton: JButton = JButton("Prethodni korak")

    init {
        layout = BorderLayout()
        preferredSize = Dimension(400, 250)

        // Set up panel for current step and progress
        val stepPanel = JPanel()
        stepPanel.layout = BoxLayout(stepPanel, BoxLayout.Y_AXIS)

        // Adjust font size to 20
        stepLabel.font = Font(stepLabel.font.name, Font.PLAIN, 20)
        stepPanel.add(stepLabel)

        // Customize progress bar size and color
        progressBar.setValue(currentStep)
        progressBar.preferredSize = Dimension(350, 80)  // Increase size
        progressBar.foreground = Color(0xDBF7EE)  // Set the color to #DBF7EE
        progressBar.background = Color(0xDBF7EE)  // Light gray background for the progress bar
        progressBar.border = BorderFactory.createEmptyBorder()  // Remove border
        stepPanel.add(progressBar)


        // Button Panel
        val buttonPanel = JPanel()
        buttonPanel.layout = FlowLayout(FlowLayout.CENTER)

        prevButton.isEnabled = false
        prevButton.addActionListener {
            if (currentStep > 0) {
                currentStep--
                updateView()
            }
        }

        nextButton.addActionListener {
            if (currentStep < steps.size - 1) {
                currentStep++
                updateView()
            }
        }

        buttonPanel.add(prevButton)
        buttonPanel.add(nextButton)

        add(stepPanel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)
    }

    private fun updateView() {
        stepLabel.text = steps[currentStep]
        progressBar.value = currentStep
        prevButton.isEnabled = currentStep > 0
        nextButton.isEnabled = currentStep < steps.size - 1
    }
}


