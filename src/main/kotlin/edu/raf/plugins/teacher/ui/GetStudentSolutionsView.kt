package edu.raf.plugins.teacher.ui

import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.utils.ImageLoader
import javax.swing.*
import java.awt.*
import java.net.URL

class GetStudentSolutionsView : JPanel() {
    private var currentStep = 0
    private val steps = arrayOf(
        "Korak 1: Izaberite predmet",
        "Korak 2: Izaberite školsku godinu",
        "Korak 3: Izaberite proveru znanja",
        "Korak 4: Izaberite grupu"
    )

    private val progressBar = JProgressBar(0, steps.size - 1)
    private val stepLabel = JLabel("", JLabel.LEFT)
    private val stepIconLabel = JLabel()
    private val nextButton = JButton("Sledeći korak")
    private val prevButton = JButton("Prethodni korak")

    private val subjectsImage = loadIcon(ConstantsUtil.SUBJECTS_IMAGE)
    private val calendarImage = loadIcon(ConstantsUtil.CALENDAR_IMAGE)
    private val examsImage = loadIcon(ConstantsUtil.EXAMS_IMAGE)
    private val groupsImage = loadIcon(ConstantsUtil.GROUPS_IMAGE)

    init {
        layout = BorderLayout()
        preferredSize = Dimension(400, 300)

        // Glavni panel sa vertikalnim rasporedom
        val mainPanel = JPanel()
        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)

        // Panel za sliku i tekst u istom redu
        val stepPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        stepIconLabel.preferredSize = Dimension(40, 40)
        stepLabel.font = Font(stepLabel.font.name, Font.PLAIN, 20)
        stepPanel.add(stepIconLabel)
        stepPanel.add(stepLabel)

        // Progress bar ispod slike i teksta, sada deblji
        progressBar.preferredSize = Dimension(350, 30)  // Deblji progress bar
        progressBar.foreground = Color(0xDBF7EE)

        mainPanel.add(stepPanel)
        mainPanel.add(Box.createVerticalStrut(10))
        mainPanel.add(progressBar)
        mainPanel.add(Box.createVerticalStrut(20))  // Veći razmak između progress bara i dugmića

        // Dugmad za navigaciju, dodatno podignuta
        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        buttonPanel.border = BorderFactory.createEmptyBorder(5, 0, 0, 0)

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

        add(mainPanel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)

        updateView()
    }

    private fun updateView() {
        stepLabel.text = steps[currentStep]
        progressBar.value = currentStep

        // Postavljanje odgovarajuće slike za trenutni korak
        stepIconLabel.icon = when (currentStep) {
            0 -> subjectsImage
            1 -> calendarImage
            2 -> examsImage
            3 -> groupsImage
            else -> null
        }

        prevButton.isEnabled = currentStep > 0
        nextButton.isEnabled = currentStep < steps.size - 1
    }

    private fun loadIcon(imagePath: String): ImageIcon {
        return ImageIcon(
            ImageIcon(URL(ImageLoader.getImageUrl(imagePath)))
                .image.getScaledInstance(40, 40, Image.SCALE_SMOOTH)
        )
    }
}
