package edu.raf.plugins.teacher.ui

import edu.raf.plugins.teacher.models.Comment
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder

class CommentsView : JPanel() {
    private val vBox = Box(BoxLayout.Y_AXIS)
    private val expandedComments = mutableSetOf<Long>()

    init {
        layout = BorderLayout()
        preferredSize = Dimension(400, 400)

        vBox.border = EmptyBorder(5, 5, 5, 5)
        add(JScrollPane(vBox), BorderLayout.CENTER)
    }

    fun updateComments(comments: List<Comment>) {
        vBox.removeAll()

        comments.forEach { comment ->
            val hBox = JPanel(BorderLayout()).apply {
                preferredSize = Dimension(380, 30)
                maximumSize = Dimension(380, if (expandedComments.contains(comment.id)) 500 else 30)

                // Kreiranje komponenti
                val dashLabel = JLabel("- ").apply {
                    font = font.deriveFont(Font.BOLD)
                }

                val fileNameLabel = JLabel(comment.relativePath).apply {
                    font = font.deriveFont(Font.BOLD)
                    foreground =  Color(0xFFB4B4)//#FFB4B4
                }

                val lineNumbersLabel = JLabel(" (${comment.startLine}-${comment.endLine}): ").apply {
                    font = font.deriveFont(Font.PLAIN)
                }

                val commentText = if (expandedComments.contains(comment.id)) {
                    comment.commentText
                } else {
                    comment.shortComment
                }

                val commentLabel = JLabel(commentText)

                // Panel za glavni sadržaj (leva strana)
                val contentPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
                    add(dashLabel)
                    add(fileNameLabel)
                    add(lineNumbersLabel)
                    add(commentLabel)
                }

                // Panel za tačkice (desna strana)
                val dotsPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
                    if (comment.commentText.length > 50) {
                        val dotsButton = JButton("...").apply {
                            font = font.deriveFont(Font.BOLD)
                            foreground = Color.GRAY
                            border = BorderFactory.createEmptyBorder()
                            preferredSize = Dimension(20, 20)
                            addActionListener {
                                if (expandedComments.contains(comment.id)) {
                                    expandedComments.remove(comment.id)
                                } else {
                                    expandedComments.add(comment.id)
                                }
                                updateComments(comments)
                            }
                        }
                        add(dotsButton)
                    }
                }

                add(contentPanel, BorderLayout.CENTER)
                add(dotsPanel, BorderLayout.EAST)
            }

            vBox.add(hBox)
            vBox.add(Box.createVerticalStrut(5))
        }

        vBox.revalidate()
        vBox.repaint()
    }
}