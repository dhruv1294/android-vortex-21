package edu.nitt.vortex21.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.viewpager2.widget.ViewPager2
import com.squareup.picasso.Picasso
import edu.nitt.vortex21.R
import edu.nitt.vortex21.databinding.FragmentStory2Binding
import edu.nitt.vortex21.databinding.FragmentStoryBinding
import edu.nitt.vortex21.helpers.viewLifecycle
import jp.shts.android.storiesprogressview.StoriesProgressView


class StoryFragment2(val storyViewListener: ViewPagerFragment.storyViewListener) : Fragment(),
    StoriesProgressView.StoriesListener {


    private lateinit var storiesProgressView:StoriesProgressView
    private lateinit var imageStory:ImageView
    private lateinit var viewPager2:ViewPager2
    private var imagesList:List<String>? = null
    private var storyids:List<String>? = null
    private var counter = MutableLiveData<Int>()
    private var pressTime = 0L
    private var limit = 5000L
    private val onTouchListener = View.OnTouchListener {view, motionEvent ->
        when(motionEvent.action){
            MotionEvent.ACTION_DOWN->
            {
                pressTime = System.currentTimeMillis()
                storiesProgressView.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP->
            {
                val now  = System.currentTimeMillis()
                storiesProgressView.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_story2,container,false)
        counter.value = 0
        storiesProgressView = view.findViewById(R.id.stories_progress2)
        (activity as AppCompatActivity).supportActionBar?.hide()
        Log.i("MyTagPagerItemCreate",requireArguments().getInt("position").toString())
        val viewPagerFragment = this.parentFragment  as ViewPagerFragment
        viewPager2 = viewPagerFragment.binding.pager
        viewPager2.currentItem = requireArguments().getInt("position")
        val navHostFragment = this.parentFragment?.parentFragment as NavHostFragment
        val parent = navHostFragment.parentFragment as HomeFragment
        parent.binding.bottomNavigation.visibility = View.INVISIBLE
        imageStory = view.findViewById(R.id.image_story2)
        getStories()
        val reverse:View = view.findViewById(R.id.reverse2)
        reverse.setOnClickListener{
            Log.i("Touched","Reverse")
            storiesProgressView.reverse()
            Log.i("reverse",counter.toString())
        }
        reverse.setOnTouchListener(onTouchListener)
        val skip:View = view.findViewById(R.id.skip2)
        skip.setOnClickListener {
            Log.i("Touched","Skipped")
            storiesProgressView.skip()
            Log.i("skip",counter.toString())
        }
        skip.setOnTouchListener(onTouchListener)
        counter.observe(viewLifecycleOwner, Observer {
            if(it == imagesList!!.size){
                storyViewListener.OnEndStory(requireArguments().getInt("position"))
            }
        })
        return view
    }



    fun getStories(){
        imagesList = ArrayList()
        storyids = ArrayList()
        (imagesList as ArrayList<String>).add("https://picsum.photos/id/1/600/700")
        (imagesList as ArrayList<String>).add("https://picsum.photos/id/2/600/700")
        (imagesList as ArrayList<String>).add("https://picsum.photos/id/3/600/700")
        storiesProgressView.setStoriesCount(imagesList!!.size)
        storiesProgressView.setStoryDuration(4000L)
        storiesProgressView.setStoriesListener(this)
        storiesProgressView.startStories(counter.value!!)
        Picasso.get().load(imagesList!![counter.value!!]).into(imageStory)
    }

    override fun onComplete() {
        counter.value = imagesList!!.size
    }


    override fun onPrev() {
        if(counter.value!! >0) {
            counter.value = (counter.value)?.minus(1)
            Picasso.get().load(imagesList!![counter.value!!]).placeholder(R.drawable.vortex_logo)
                .into(imageStory)
            Log.i("MyTagPrev",counter.toString())
        }
    }

    override fun onNext() {
        if(counter.value!!<imagesList!!.size) {
            counter.value = (counter.value)?.plus(1)
            Picasso.get().load(imagesList!![counter.value!!]).placeholder(R.drawable.vortex_logo)
                .into(imageStory)
            Log.i("MyTagNext",counter.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        storiesProgressView.resume()
    }

    override fun onPause() {
        super.onPause()
        storiesProgressView.pause()
    }

    override fun onDestroy() {

        storiesProgressView.destroy();
        super.onDestroy();
    }


}