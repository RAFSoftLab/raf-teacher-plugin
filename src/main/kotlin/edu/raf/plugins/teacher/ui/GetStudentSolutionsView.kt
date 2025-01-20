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

    private val options = arrayOf(
        arrayOf("OOP", "DSW", "TS", "Web Programming"),
        arrayOf("2022/23", "2023/24", "2024/25"),
        arrayOf("Prvi test", "Test", "Kolokvijum"),
        arrayOf("Grupa 1", "Grupa 2", "Grupa 3")
    )

    private val selectedOptions = mutableMapOf<Int, String>()
    private val comboBox = JComboBox<String>()

    private val subjectsIcon = ImageLoader.loadIcon(ConstantsUtil.SUBJECTS_IMAGE, 30, 30)
    private val calendarIcon = ImageLoader.loadIcon(ConstantsUtil.CALENDAR_IMAGE, 30, 30)
    private val examsIcon = ImageLoader.loadIcon(ConstantsUtil.EXAMS_IMAGE, 30, 30)
    private val groupsIcon = ImageLoader.loadIcon(ConstantsUtil.GROUPS_IMAGE, 30, 30)

    private val prevIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.PREVIOUS_IMAGE)))
    private val nextIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.NEXT_IMAGE)))

    private val progressBar = JProgressBar(0, steps.size - 1)
    private val stepLabel = JLabel("", JLabel.LEFT)
    private val stepIconLabel = JLabel()

    private val prevButton: JButton = JButton("Prethodni korak").apply {
        font = font.deriveFont(Font.ITALIC, 12f)
        preferredSize = Dimension(120, 30)
        val resizedIcon = ImageIcon(prevIcon.image.getScaledInstance(16, 16, Image.SCALE_SMOOTH))
        icon = resizedIcon
    }

    private val nextButton: JButton = JButton("Sledeći korak").apply {
        font = font.deriveFont(Font.ITALIC, 12f)
        preferredSize = Dimension(120, 30)
        val resizedIcon = ImageIcon(nextIcon.image.getScaledInstance(16, 16, Image.SCALE_SMOOTH))
        icon = resizedIcon
        horizontalTextPosition = SwingConstants.LEADING
        verticalTextPosition = SwingConstants.CENTER
    }

    init {
        layout = BorderLayout()
        preferredSize = Dimension(400, 300)

        val topPanel = JPanel()
        topPanel.layout = BoxLayout(topPanel, BoxLayout.Y_AXIS)
        topPanel.alignmentX = Component.LEFT_ALIGNMENT

        val iconTextPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        stepIconLabel.preferredSize = Dimension(32, 32)
        stepLabel.font = Font(stepLabel.font.name, Font.PLAIN, 20)

        iconTextPanel.add(stepIconLabel)
        iconTextPanel.add(Box.createHorizontalStrut(10))
        iconTextPanel.add(stepLabel)

        progressBar.preferredSize = Dimension(350, 15)
        progressBar.foreground = Color(0xDBF7EE)

        topPanel.add(iconTextPanel)
        topPanel.add(Box.createVerticalStrut(5))
        topPanel.add(progressBar)

        val centerPanel = JPanel()
        centerPanel.layout = GridBagLayout()
        val gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.weightx = 0.1  // 60% širine
        gbc.insets = Insets(10, 0, 10, 0) // Opcioni razmaci
        centerPanel.add(comboBox, gbc)

        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        buttonPanel.border = BorderFactory.createEmptyBorder(25, 0, 0, 0)
        buttonPanel.add(prevButton)
        buttonPanel.add(nextButton)

        add(topPanel, BorderLayout.NORTH)
        add(centerPanel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)

        prevButton.isEnabled = false
        prevButton.addActionListener {
            if (currentStep > 0) {
                selectedOptions[currentStep] = comboBox.selectedItem as String
                currentStep--
                updateView()
            }
        }

        nextButton.addActionListener {
            if (currentStep < steps.size - 1) {
                selectedOptions[currentStep] = comboBox.selectedItem as String
                currentStep++
                updateView()
            }
        }

        updateView()
    }

    private fun updateView() {
        stepLabel.text = steps[currentStep]
        progressBar.value = currentStep

        stepIconLabel.icon = when (currentStep) {
            0 -> subjectsIcon
            1 -> calendarIcon
            2 -> examsIcon
            3 -> groupsIcon
            else -> null
        }

        comboBox.removeAllItems()
        options[currentStep].forEach { comboBox.addItem(it) }

        comboBox.selectedItem = selectedOptions[currentStep] ?: options[currentStep][0]

        prevButton.isEnabled = currentStep > 0
        nextButton.isEnabled = currentStep < steps.size - 1
    }
}
