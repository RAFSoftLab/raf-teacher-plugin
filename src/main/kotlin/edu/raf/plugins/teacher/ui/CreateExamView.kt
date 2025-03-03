package edu.raf.plugins.teacher.ui

import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.listeners.ExamViewListener
import edu.raf.plugins.teacher.models.Subject
import edu.raf.plugins.teacher.ui.UIUtils.Companion.addHint
import edu.raf.plugins.teacher.utils.ImageLoader
import edu.raf.plugins.teacher.utils.Utils.Companion.generateSchoolYear
import java.awt.*
import java.net.URL
import java.time.LocalDate
import javax.swing.*

class CreateExamView : JPanel() {
    var listener: ExamViewListener? = null

    // Komponente za View
    private val comboBoxSubjects: JComboBox<Subject> = JComboBox()
    private val labelChooseSubject: JLabel = JLabel("Izaberite predmet:")

    private val currentYearLabel: JLabel = JLabel("Tekuća godina:")
    private val currentYearInput: JTextField = JTextField(generateSchoolYear(LocalDate.now()), 7)

    private val testNameLabel: JLabel = JLabel("Naziv provere znanja:")
    private val testNameInput: JTextField = JTextField(12).apply {
        toolTipText = "Prvi kolokvijum"
        addHint("Prvi kolokvijum...")
    }

    private val groupLabel: JLabel = JLabel("Grupa:")
    private val groupInput: JTextField = JTextField(6)


    private val uploadExamIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.UPLOAD_IMAGE)))
    private val backIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.BACK_IMAGE)))

    private val submitButton: JButton = JButton("Unesi").apply {
        icon = ImageIcon(uploadExamIcon.image.getScaledInstance(46, 46, Image.SCALE_SMOOTH)) // Smanjenje na 46x46
        isEnabled = false // Dugme je inicijalno onemogućeno
    }


    // Novo dugme za povratak na glavni meni
    private val backToMenuButton: JButton = JButton("Vrati se na glavni menu").apply {
        font = font.deriveFont(Font.ITALIC, 12f) // Manji font, stil Italic
        preferredSize = Dimension(100, 30) // Manje dugme
        val resizedIcon = ImageIcon(backIcon.image.getScaledInstance(16, 16, Image.SCALE_SMOOTH)) // Smanjivanje ikone
        icon = resizedIcon
    }

    private val progressBar: JProgressBar = JProgressBar().apply {
        isIndeterminate = true
        preferredSize = Dimension(200, 20)
        isVisible = false  // Početno je nevidljiv
        foreground = Color(0xDBF7EE)  // Postavljanje boje napretka
        background = Color(0xE1E1E1)  // Postavljanje svetlo sive boje za pozadinu
    }




    init {
        layout = GridBagLayout()

        val constraints = GridBagConstraints()
        constraints.fill = GridBagConstraints.HORIZONTAL
        constraints.insets = Insets(5, 5, 5, 5)

        // Prvi red (label + comboBox)
        constraints.gridx = 0
        constraints.gridy = 0
        add(labelChooseSubject, constraints)

        constraints.gridx = 1
        add(comboBoxSubjects, constraints)

        // Drugi red (label + godina)
        constraints.gridx = 0
        constraints.gridy = 1
        add(currentYearLabel, constraints)

        constraints.gridx = 1
        add(currentYearInput, constraints)

        // Treći red (label + testName)
        constraints.gridx = 0
        constraints.gridy = 2
        add(testNameLabel, constraints)

        constraints.gridx = 1
        add(testNameInput, constraints)

        // Četvrti red (label + grupa)
        constraints.gridx = 0
        constraints.gridy = 3
        add(groupLabel, constraints)

        constraints.gridx = 1
        add(groupInput, constraints)

        // Peti red (dugme za unos)
        constraints.gridx = 0
        constraints.gridy = 4
        constraints.gridwidth = 2
        add(submitButton, constraints)

        // Progres bar
        constraints.gridx = 0
        constraints.gridy = 6
        constraints.gridwidth = 2
        add(progressBar, constraints)


        // Šesti red (dugme za povratak na meni)
        constraints.gridx = 0
        constraints.gridy = 5
        constraints.gridwidth = 2
        add(backToMenuButton, constraints)




        // Postavljanje prilagođenog renderera za comboBox
        comboBoxSubjects.renderer = object : ListCellRenderer<Subject> {
            private val renderer = DefaultListCellRenderer()
            override fun getListCellRendererComponent(
                list: JList<out Subject>?, value: Subject?, index: Int, isSelected: Boolean, cellHasFocus: Boolean
            ): Component {
                val component =
                    renderer.getListCellRendererComponent(list, value?.name, index, isSelected, cellHasFocus)
                (component as JLabel).toolTipText = value?.shortName
                return component
            }
        }

        // Akcija na dugme "Unesi"
        submitButton.addActionListener {
            val selectedSubject = comboBoxSubjects.selectedItem as? Subject
            val year = currentYearInput.text
            val testName = testNameInput.text
            val group = groupInput.text

            if (selectedSubject == null || year.isBlank() || testName.isBlank()) {
                JOptionPane.showMessageDialog(
                    null, "Popunite sva polja.", "Greška", JOptionPane.ERROR_MESSAGE
                )
            } else {
                listener?.onSubmitExam(selectedSubject, year, testName, group)
            }
        }


        // Akcija na dugme "Vrati se na glavni menu"
        backToMenuButton.addActionListener {
            val parentPanel = this.parent as? JPanel
            val cardLayout = parentPanel?.layout as? CardLayout
            cardLayout?.show(parentPanel, "Menu")

        }
    }

    fun updateSubjects(subjects: List<Subject>) {
        comboBoxSubjects.removeAllItems()
        subjects.forEach { subject -> comboBoxSubjects.addItem(subject) }
    }

    fun showLoader(isVisible: Boolean) {
        progressBar.isVisible = isVisible
    }

    fun enableSubmitButton() {
        submitButton.isEnabled = true
    }

    fun disableSubmitButton() {
        submitButton.isEnabled = false
    }
}
