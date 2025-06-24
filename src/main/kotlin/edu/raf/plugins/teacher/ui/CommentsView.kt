package edu.raf.plugins.teacher.ui

import edu.raf.plugins.teacher.constants.ConstantsUtil
import edu.raf.plugins.teacher.listeners.DeleteCommentListener
import edu.raf.plugins.teacher.listeners.EditCommentListener
import edu.raf.plugins.teacher.models.Comment
import edu.raf.plugins.teacher.utils.ImageLoader
import java.awt.*
import java.net.URL
import javax.swing.*
import javax.swing.border.*

class CommentsView : JPanel() {
    var listenerEdit: EditCommentListener? = null
    var listenerDelete: DeleteCommentListener? = null

    private val vBox = Box(BoxLayout.Y_AXIS)
    private val expandedComments = mutableSetOf<Long>()
    private val commentBorder = CompoundBorder(
        MatteBorder(0, 0, 1, 0, Color(0xEEEEEE)),
        EmptyBorder(5, 5, 5, 5)
    )
    private val trashIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.TRASH_IMAGE)))
    private val editIcon = ImageIcon(URL(ImageLoader.getImageUrl(ConstantsUtil.EDIT_IMAGE)))

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
                maximumSize = Dimension(380, if (expandedComments.contains(comment.id)) Short.MAX_VALUE.toInt() else 30)
                border = commentBorder

                // Main content components
                val dashLabel = JLabel("- ").apply {
                    font = font.deriveFont(Font.BOLD)
                }

                val fileNameLabel = JLabel(comment.relativePath).apply {
                    font = font.deriveFont(Font.BOLD)
                    foreground = Color(0xFFB4B4)
                }

                val lineNumbersLabel = JLabel(" (${comment.startLine}-${comment.endLine}): ").apply {
                    font = font.deriveFont(Font.PLAIN)
                }

                // Use JTextArea for expanded comments to show full text with wrapping
                val commentComponent = if (expandedComments.contains(comment.id)) {
                    JScrollPane(JTextArea(comment.commentText).apply {
                        lineWrap = true
                        wrapStyleWord = true
                        isEditable = false
                        background = null
                        border = null
                        font = Font("Dialog", Font.PLAIN, 12)
                    }).apply {
                        preferredSize = Dimension(350, 100)
                        border = null
                    }
                } else {
                    JLabel(
                        if (comment.commentText.length > 35)
                            "${comment.commentText.substring(0, 35)}..."
                        else comment.commentText
                    )
                }

                // Left content panel
                val leftPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
                    add(dashLabel)
                    add(fileNameLabel)
                    add(lineNumbersLabel)
                }

                // Main content panel with BorderLayout
                val contentPanel = JPanel(BorderLayout(5, 0)).apply {
                    add(leftPanel, BorderLayout.WEST)
                    add(commentComponent, BorderLayout.CENTER)
                }

                // Right buttons panel
                val buttonsPanel = JPanel(FlowLayout(FlowLayout.RIGHT, 5, 0)).apply {
                    // Expand/collapse button
                    if (comment.commentText.length > 35) {
                        val dotsButton = JButton("...").apply {
                            font = font.deriveFont(Font.BOLD)
                            foreground = Color.GRAY
                            border = BorderFactory.createEmptyBorder()
                            preferredSize = Dimension(20, 20)
                            addActionListener {
                                expandedComments.toggle(comment.id)
                                updateComments(comments)
                            }
                        }
                        add(dotsButton)
                    }

                    // Edit button
                    val editButton = createIconButton(editIcon, 20) {
                        // Edit action
                        listenerEdit?.onEditComment(comment)
                    }
                    add(editButton)

                    // Delete button
                    val deleteButton = createIconButton(trashIcon, 20) {
                        // Delete action
                        listenerDelete?.onDeleteComment(comment)
                    }
                    add(deleteButton)
                }

                add(contentPanel, BorderLayout.CENTER)
                add(buttonsPanel, BorderLayout.EAST)
            }

            vBox.add(hBox)
            vBox.add(Box.createVerticalStrut(5))
        }

        vBox.revalidate()
        vBox.repaint()
    }

    private fun createIconButton(
        icon: Icon,
        size: Int,
        action: () -> Unit
    ): JButton {
        val scaledIcon = ImageIcon(
            (icon as? ImageIcon)?.image?.getScaledInstance(size, size, Image.SCALE_SMOOTH)
        )

        return JButton(scaledIcon).apply {
            border = BorderFactory.createEmptyBorder()
            preferredSize = Dimension(size + 1, size + 1)
            isContentAreaFilled = false
            isFocusPainted = false
            addActionListener { action() }
        }
    }


    private fun MutableSet<Long>.toggle(id: Long) {
        if (contains(id)) remove(id) else add(id)
    }
}