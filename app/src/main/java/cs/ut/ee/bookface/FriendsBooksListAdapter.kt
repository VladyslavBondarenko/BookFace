package cs.ut.ee.bookface

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

import java.util.*


class FriendsBooksListAdapter(
    var c: Context,
    var books_list: ArrayList<HashMap<String, Any>>,
    var user_id: String
) : BaseExpandableListAdapter() {
    override fun getGroup(position: Int): HashMap<String, Any> {
        return books_list[position]
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view: View
        view = if (convertView == null) {
            val layoutInflater = LayoutInflater.from(c)
            layoutInflater.inflate(R.layout.friends_books_parent, parent, false)
        } else {
            convertView
        }
        val book: HashMap<String, Any> = getGroup(groupPosition)
        view.findViewById<TextView>(R.id.book_title2).text = book["title"] as String
        view.findViewById<TextView>(R.id.book_author2).text = book["author"] as String

        var bookAvailable = c.getString(R.string.bookAvailable)
        var bookTaken: String = c.getString(R.string.bookTaken)

        if (book["isAvailable"] as Boolean) {
            view.findViewById<TextView>(R.id.is_available2).text = bookAvailable
            view.findViewById<TextView>(R.id.is_available2)
                .setTextColor(Color.parseColor("#008000"))
        } else {
            view.findViewById<TextView>(R.id.is_available2).text = bookTaken
            view.findViewById<TextView>(R.id.is_available2)
                .setTextColor(Color.parseColor("#FF8C00"))
        }
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return books_list[groupPosition][books_list[groupPosition].keys.elementAt(childPosition)]!!
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view: View
        view = if (convertView == null) {
            val layoutInflater = LayoutInflater.from(c)
            layoutInflater.inflate(R.layout.friends_books_child, parent, false)
        } else {
            convertView
        }
        val book: HashMap<String, Any> = getGroup(groupPosition)
        view.findViewById<TextView>(R.id.expandedListItem).text = book["description"] as String
        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return books_list.size
    }

}