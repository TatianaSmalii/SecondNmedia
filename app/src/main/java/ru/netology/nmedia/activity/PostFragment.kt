package ru.netology.nmedia.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.EditPostFragment.Companion.contentArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.viewmodel.PostViewModel


class PostFragment : Fragment() {
    companion object {
        var Bundle.idArg: Long? by LongArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        val viewModel: PostViewModel by activityViewModels()

        val id = requireArguments().idArg

        val adapter = PostsAdapter(object : OnInteractionListener {
            // функция редактирования
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                // переход между фрагментами с передачей текста поста с ключом contentArg
                findNavController().navigate(
                    R.id.action_postFragment_to_editPostFragment,
                    Bundle().apply {
                        contentArg = post.content
                    })
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigateUp()
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(intent, null)
                startActivity(shareIntent)
                viewModel.share(post.id)
            }

            // открытие ссылки в youtube по клику на кнопку и поле картинки
//            override fun openVideo(post: Post) {
//                val webpage: Uri = Uri.parse(post.video)
//                val intent = Intent(Intent.ACTION_VIEW, webpage)
//                try {
//                    startActivity(intent)
//                } catch (e: ActivityNotFoundException) {
//                    Toast.makeText(context, "No suitable app found!", Toast.LENGTH_SHORT).show()
//                }
//            }
        }
        )

        //binding.listOfOnePost.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts.filter { it.id == id })
        }

//        viewModel.data.observe(viewLifecycleOwner)
//        { posts ->
//            val listOfOnePost = posts //.filter { it.id == id }
//            if (listOfOnePost.isEmpty()) {
//                findNavController().navigateUp()
//                return@observe
//            }
//            adapter.submitList(listOfOnePost)
//        }

        return binding.root
    }
}