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


    //Ponudjeno korisnicima, cita se iz APIja
    private val options = arrayOf(
        arrayOf("OOP", "DSW", "TS", "Web Programming"),
        arrayOf("2022/23", "2023/24", "2024/25"),
        arrayOf("Prvi test", "Test", "Kolokvijum"),
        arrayOf("Grupa 1", "Grupa 2", "Grupa 3")
    )

    // Opcija za konformaciju izabranih opcija

    private val confirmedOptionsTitles = mapOf(
        0 to "Izabrani predmet:",
        1 to "Izabrana godina:",
        2 to "Izabrana provera znanja:",
        3 to "Izabrana grupa:"
    )


    private val selectedOptions = mutableMapOf<Int, String>() // Ono sto je korisnik izbarao
    private val comboBoxOptions = JComboBox<String>()

    private val subjectsIcon = ImageLoader.loadIcon(ConstantsUtil.SUBJECTS_IMAGE, 30, 30)
    private val calendarIcon = ImageLoader.loadIcon(ConstantsUtil.CALENDAR_IMAGE, 30, 30)
    private val examsIcon = ImageLoader.loadIcon(ConstantsUtil.EXAMS_IMAGE, 30, 30)
    private val groupsIcon = ImageLoader.loadIcon(ConstantsUtil.GROUPS_IMAGE, 30, 30)

    private val backIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.BACK_IMAGE)))
    private val downloadSolutionIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.DOWNLOAD_IMAGE)))

    private val prevIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.PREVIOUS_IMAGE)))
    private val nextIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.NEXT_IMAGE)))

    private val stepBar = JProgressBar(0, steps.size - 1)
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

    private val backToMenuButton: JButton = JButton("Vrati se na glavni menu").apply {
        font = font.deriveFont(Font.ITALIC, 12f) // Manji font, stil Italic
        preferredSize = Dimension(100, 30) // Manje dugme
        val resizedIcon = ImageIcon(backIcon.image.getScaledInstance(16, 16, Image.SCALE_SMOOTH)) // Smanjivanje ikone
        icon = resizedIcon
    }


    private val submitButton: JButton = JButton("Preuzmi").apply {
        icon = ImageIcon(downloadSolutionIcon.image.getScaledInstance(46, 46, Image.SCALE_SMOOTH)) // Smanjenje na 46x46
        isVisible = false  // Na početku je sakriveno
        isEnabled = false // Dugme je inicijalno onemogućeno
    }

    private val progressBar: JProgressBar = JProgressBar().apply {
        isIndeterminate = true
        preferredSize = Dimension(200, 20)
        isVisible = false  // Početno je nevidljiv
        foreground = Color(0xDBF7EE)  // Postavljanje boje napretka
        background = Color(0xE1E1E1)  // Postavljanje svetlo sive boje za pozadinu
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

        stepBar.preferredSize = Dimension(350, 15)
        stepBar.foreground = Color(0xDBF7EE)

        topPanel.add(iconTextPanel)
        topPanel.add(Box.createVerticalStrut(5))
        topPanel.add(stepBar)

        val centerPanel = JPanel()
        centerPanel.layout = GridBagLayout()
        val gbc = GridBagConstraints()
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridx = 0
        gbc.weightx = 1.0
        gbc.insets = Insets(5, 20, 5, 20)

        // Dodajemo prazan prostor na vrhu da bi comboBoxOptions bio bliže sredini
        gbc.gridy = 0
        gbc.weighty = 0.5
        centerPanel.add(Box.createVerticalGlue(), gbc)

        // ComboBox pozicioniran bliže sredini
        gbc.gridy = 1
        gbc.weighty = 0.0
        centerPanel.add(comboBoxOptions, gbc)

        gbc.gridy = 2
        gbc.insets = Insets(10, 20, 10, 20)
        centerPanel.add(submitButton, gbc)

        gbc.gridy = 3
        centerPanel.add(progressBar, gbc)

        // Prazan prostor da gurne dugmad ka dnu
        gbc.gridy = 4
        gbc.weighty = 1.0
        centerPanel.add(Box.createVerticalGlue(), gbc)

        // Dugmad za navigaciju
        val stepsButtonPanel = JPanel(FlowLayout(FlowLayout.CENTER, 10, 0))
        stepsButtonPanel.add(prevButton)
        stepsButtonPanel.add(nextButton)

        gbc.gridy = 5
        gbc.weighty = 0.0
        gbc.insets = Insets(20, 20, 10, 20)
        centerPanel.add(stepsButtonPanel, gbc)

        // Dugme "Nazad na meni" sa još više razmaka od stepsPanel-a
        gbc.gridy = 6
        gbc.insets = Insets(30, 20, 10, 20)
        centerPanel.add(backToMenuButton, gbc)

        add(topPanel, BorderLayout.NORTH)
        add(centerPanel, BorderLayout.CENTER)

        prevButton.isEnabled = false
        prevButton.addActionListener {
            if (currentStep > 0) {
                selectedOptions[currentStep] = comboBoxOptions.selectedItem as String
                currentStep--
                updateView()
            }
        }

        nextButton.addActionListener {
            if (currentStep < steps.size - 1) {
                selectedOptions[currentStep] = comboBoxOptions.selectedItem as String
                currentStep++
                updateView()
            }
        }

        backToMenuButton.addActionListener {
            val parentPanel = this.parent as? JPanel
            val cardLayout = parentPanel?.layout as? CardLayout
            cardLayout?.show(parentPanel, "Menu")
        }

        submitButton.addActionListener {
            selectedOptions[currentStep] = comboBoxOptions.selectedItem as String

            val selectedValues = steps.mapIndexed { index, step ->
                "${confirmedOptionsTitles[index]} ${selectedOptions[index]}"
            }.joinToString("\n")

            val options = arrayOf("Da", "Ne")
            val confirmation = JOptionPane.showOptionDialog(
                this@GetStudentSolutionsView,
                "Da li želite da preuzmete sledeću proveru znanja?\n\n$selectedValues",
                "Potvrda preuzimanja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            )


            if (confirmation == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(
                    this@GetStudentSolutionsView,
                    "Uspešno preuzeto!",
                    "Obaveštenje",
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        }

        updateView()
    }



    // Ažuriranje vidljivosti submit dugmeta u updateView()
    private fun updateView() {
        stepLabel.text = steps[currentStep]
        stepBar.value = currentStep

        stepIconLabel.icon = when (currentStep) {
            0 -> subjectsIcon
            1 -> calendarIcon
            2 -> examsIcon
            3 -> groupsIcon
            else -> null
        }

        comboBoxOptions.removeAllItems()
        options[currentStep].forEach { comboBoxOptions.addItem(it) }
        comboBoxOptions.selectedItem = selectedOptions[currentStep] ?: options[currentStep][0]

        prevButton.isEnabled = currentStep > 0
        nextButton.isEnabled = currentStep < steps.size - 1

        // Prikazuje submit dugme samo kada je poslednji korak
        submitButton.isVisible = currentStep == steps.size - 1
    }

    fun showLoader(isVisible: Boolean) {
        progressBar.isVisible = isVisible
    }

    fun enableSubmitButton() {
        submitButton.isEnabled = true
    }
}
